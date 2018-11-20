package com.redhat.codeready.selenium.performance;

import com.google.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.che.selenium.core.client.CheTestWorkspaceServiceClient;
import org.eclipse.che.selenium.core.executor.OpenShiftCliCommandExecutor;
import org.eclipse.che.selenium.core.user.DefaultTestUser;
import org.eclipse.che.selenium.core.user.TestUser;
import org.eclipse.che.selenium.core.workspace.CheTestWorkspaceProvider;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.eclipse.che.selenium.core.workspace.WorkspaceTemplate;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StartWorkspaceStatistic {

  @Inject private CheTestWorkspaceProvider testWorkspace;

  @Inject private CheTestWorkspaceServiceClient testWorkspaceServiceClient;

  @Inject private   DefaultTestUser user;

  @Inject private OpenShiftCliCommandExecutor openShiftCliCommandExecutor;

  TestWorkspace createdWorkspace;
  private static final String PATTERN_PREFFIX = "\\d\\d\\d\\d.*";
  private String patternPreffixFinishingCreatingPod =
      PATTERN_PREFFIX + "Pods creation finished in workspace.*%s.*";
  private String patternPreffixStartingWs = PATTERN_PREFFIX + "Machines of workspace.*%s.* started";

  private String logsFromAssociatedWorkspace;
  private List<Long> startingTimeInSecondsStatistic = new ArrayList<>();

  @BeforeMethod
  private void createWs() throws Exception {
    createdWorkspace = testWorkspace.createWorkspace(user, 2, WorkspaceTemplate.DEFAULT, true);
  }

  @AfterMethod
  private void removeWs() throws Exception {
    testWorkspaceServiceClient.delete(createdWorkspace.getName(), user.getName());
  }

  @Test(invocationCount = 2)
  public void startWsMeasure()
      throws IOException, ExecutionException, InterruptedException, ParseException {
    String logsFromAssociatedWorkspace = getDebugLogsFromAssociatedWorksace();
    String logSubstringWithPodCreationMessage =
        getStringWithStartingWsDataFromLog(
            patternPreffixFinishingCreatingPod,
            createdWorkspace.getId(),
            logsFromAssociatedWorkspace);
    String logSubstringWithStartingWorkspaceMessage =
        getStringWithStartingWsDataFromLog(
            patternPreffixStartingWs, createdWorkspace.getId(), logsFromAssociatedWorkspace);
    Date timeOfPodCreation = getDateFromString(logSubstringWithPodCreationMessage);
    Date timeOfWorkspaceCreation = getDateFromString(logSubstringWithStartingWorkspaceMessage);
    startingTimeInSecondsStatistic.add(
        getWorkspaceTimeCreation(timeOfPodCreation, timeOfWorkspaceCreation));
    for (Long startTime : startingTimeInSecondsStatistic) {
      writeDataToTheFile(
          String.format(
              "%s:%s\n",
              new SimpleDateFormat("yyyy-MM").format(timeOfPodCreation), startTime.toString()));
    }
  }

  private String getDebugLogsFromAssociatedWorksace()
      throws IOException, ExecutionException, InterruptedException {
    String commandFromGetLogs =
        String.format("logs %s | grep [DEBUG].*%s", getPodWithLogs(), createdWorkspace.getId());
    return logsFromAssociatedWorkspace = openShiftCliCommandExecutor.execute(commandFromGetLogs);
  }

  private String getPodWithLogs() throws IOException {
    return openShiftCliCommandExecutor.execute(
        "get pods | grep codeready-'[0-9]'| awk '{print $1}'");
  }

  private Date getDateFromString(String logString) throws ParseException {
    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return parser.parse(logString);
  }

  private String getStringWithStartingWsDataFromLog(String patternPref, String wsId, String log) {
    Matcher matcher = Pattern.compile(String.format(patternPref, wsId)).matcher(log);
    if (matcher.find()) {
      return matcher.group();
    } else {
      throw new RuntimeException("The expected message in the log has not been found.");
    }
  }

  private long getWorkspaceTimeCreation(Date podCreation, Date startingWs) {
    return (startingWs.getTime() - podCreation.getTime()) / 1000;
  }

  private void writeDataToTheFile(String csvData) throws IOException {
    Path filePath = Paths.get("loadWsData.csv");
    if (!Files.exists(filePath)) {
      Files.createFile(filePath);
    }
    Files.write(filePath, csvData.getBytes(), StandardOpenOption.APPEND);
  }
}
