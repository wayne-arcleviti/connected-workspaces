package com.redhat.codeready.selenium.performance;

import com.google.inject.Inject;

import org.eclipse.che.selenium.core.executor.OpenShiftCliCommandExecutor;
import org.eclipse.che.selenium.core.workspace.TestWorkspace;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartWorkspaceStatistic {
    @Inject
    private TestWorkspace testWorkspace;

    @Inject
    private OpenShiftCliCommandExecutor openShiftCliCommandExecutor;

    private static final String PATTERN_PREFFIX_FOR_STARTING_STRING = "\\d\\d\\d\\d.*";
    private              String patternPreffixFinishingCreatingPod  =
            PATTERN_PREFFIX_FOR_STARTING_STRING + "Pods creation finished workspace.*%s.*";
    private              String patternPreffixStartingWs            =
            PATTERN_PREFFIX_FOR_STARTING_STRING + "Machines of workspace.*%s.* started";

    private String     dateFromLog;
    private String     logsFromAssociatedWorkspace;
    private List<Long> startingTimeInSecondsStatistic;

    @Test()
    public void startWsMeasure() throws IOException, ExecutionException, InterruptedException, ParseException {
        String logsFromAssociatedWorkspace = getDebugLogsFromAssociatedWorksace();
        String logSubstringWithPodCreationMessage = getStringWithStartingWsDataFromLog(PATTERN_PREFFIX_FOR_STARTING_STRING, testWorkspace.getId(), logsFromAssociatedWorkspace);
        String logSubstringWithStartingWorkspaceMessage = getStringWithStartingWsDataFromLog(PATTERN_PREFFIX_FOR_STARTING_STRING, testWorkspace.getId(), logsFromAssociatedWorkspace);
        Date timeOfPodCreation = getDateFromString(logSubstringWithPodCreationMessage);
        Date timeOfWorkspaceCreation = getDateFromString(logSubstringWithStartingWorkspaceMessage);
        startingTimeInSecondsStatistic.add(getWorkspaceTimeCreation(timeOfPodCreation,timeOfWorkspaceCreation));
    }


    private String getDebugLogsFromAssociatedWorksace() throws IOException, ExecutionException, InterruptedException {
        String commandFromGetLogs = String.format("oc %s | grep [DEBUG].*%s", getPodWithLogs(), testWorkspace.getId());
        return logsFromAssociatedWorkspace = openShiftCliCommandExecutor.execute(commandFromGetLogs);
    }

    private String getPodWithLogs() throws IOException {
        return openShiftCliCommandExecutor.execute("get pods | grep codeready-'[0-9]'");
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

    private long getWorkspaceTimeCreation(Date podCreation, Date startingWs){
        return startingWs.getTime()-podCreation.getTime();
    }

}
