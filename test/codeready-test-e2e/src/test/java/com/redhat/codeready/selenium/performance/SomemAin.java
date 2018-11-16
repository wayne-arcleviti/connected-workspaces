package com.redhat.codeready.selenium.performance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SomemAin {
    public static void main(String[] args) throws ParseException {
        String pat =
                "2018-11-15 13:10:29,120[aceSharedPool-0]  [DEBUG] [penShiftEnvironmentProvisioner 102]  - DEBUG Start provisioning OpenShift environment for workspace 'workspaceq1k2xi3o162k1jt9'2018-11-15 13:10:29,258[aceSharedPool-0]  [DEBUG] [n.p.UniqueWorkspacePVCStrategy 99]   - Provisioning PVC strategy for workspace 'workspaceq1k2xi3o162k1jt9 2018-11-15 13:10:29,413[aceSharedPool-0]  [DEBUG] [n.p.UniqueWorkspacePVCStrategy 116]  - PVC strategy provisioning done for workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:10:29,418[aceSharedPool-0]  [DEBUG] [penShiftEnvironmentProvisioner 126]  - Provisioning OpenShift environment done for workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:10:29,418[aceSharedPool-0]  [DEBUG] [.i.k.KubernetesInternalRuntime 183]  - Provisioning of workspace 'workspaceq1k2xi3o162k1jt9' completed.\n" +
                "2018-11-15 13:10:29,418[aceSharedPool-0]  [DEBUG] [n.p.UniqueWorkspacePVCStrategy 126]  - Preparing PVC started for workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:10:29,465[aceSharedPool-0]  [DEBUG] [n.p.UniqueWorkspacePVCStrategy 132]  - Preparing PVC done for workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:10:29,687[aceSharedPool-0]  [DEBUG] [.i.k.KubernetesInternalRuntime 603]  - Begin pods creation for workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:10:30,101[aceSharedPool-0]  [DEBUG] [.i.k.KubernetesInternalRuntime 606]  - Creating pod 'workspaceq1k2xi3o162k1jt9.dockerimage' in workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:10:30,102[aceSharedPool-0]  [DEBUG] [.i.k.KubernetesInternalRuntime 611]  - Creating machine 'dev-machine' in workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:10:30,356[aceSharedPool-0]  [DEBUG] [.i.k.KubernetesInternalRuntime 625]  - .*Pods creation finished workspace.* in workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:10:30,453[aceSharedPool-0]  [DEBUG] [.i.k.KubernetesInternalRuntime 314]  - Waiting to start machines of workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:10:46,322[ineSharedPool-0]  [DEBUG] [.i.k.KubernetesInternalRuntime 417]  - Bootstrapping machine 'dev-machine' in workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:10:46,327[ineSharedPool-0]  [DEBUG] [w.i.k.b.KubernetesBootstrapper 153]  - Bootstrapping RuntimeIdentityImpl{workspaceId='workspaceq1k2xi3o162k1jt9', envName='default', ownerId='33693597-683a-41b4-9eeb-3ddc247f9f00'}:dev-machine. Creating folder for bootstrapper\n" +
                "2018-11-15 13:10:46,480[ineSharedPool-0]  [DEBUG] [w.i.k.b.KubernetesBootstrapper 157]  - Bootstrapping RuntimeIdentityImpl{workspaceId='workspaceq1k2xi3o162k1jt9', envName='default', ownerId='33693597-683a-41b4-9eeb-3ddc247f9f00'}:dev-machine. Downloading bootstrapper binary\n" +
                "2018-11-15 13:10:46,898[ineSharedPool-0]  [DEBUG] [w.i.k.b.KubernetesBootstrapper 167]  - Bootstrapping RuntimeIdentityImpl{workspaceId='workspaceq1k2xi3o162k1jt9', envName='default', ownerId='33693597-683a-41b4-9eeb-3ddc247f9f00'}:dev-machine. Creating config file\n" +
                "2018-11-15 13:10:59,659[ineSharedPool-0]  [DEBUG] [.i.k.KubernetesInternalRuntime 367]  - Performing servers check for machine 'dev-machine' in workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:11:06,432[ServersChecker]   [DEBUG] [.i.k.KubernetesInternalRuntime 376]  - Servers checks done for machine 'dev-machine' in workspace 'workspaceq1k2xi3o162k1jt9'\n" +
                "2018-11-15 13:11:06,433[aceSharedPool-0]  [DEBUG] [.i.k.KubernetesInternalRuntime 328]  - Machines of workspace 'workspaceq1k2xi3o162k1jt9' started\n";

        Pattern p = Pattern.compile(String.format("\\d\\d\\d\\d.*Machines of workspace.*%s.* started", "workspaceq1k2xi3o162k1jt9"));
        Matcher matcher = p.matcher(pat);
        if (matcher.find()) {
            System.out.println(matcher.group());
        }

    }
}
