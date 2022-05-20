package org.teamapps.ux.session;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.teamapps.uisession.TeamAppsSessionManager;
import org.teamapps.uisession.UiSessionState;

public class TeamAppsSessionMetrics implements MeterBinder {

    private static final String ACTIVITY_STATE_TAG = "state";

    private final TeamAppsSessionManager sessionManager;

    public TeamAppsSessionMetrics(TeamAppsSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void bindTo(MeterRegistry registry) {

        Gauge.builder("teamapps.uisession", () -> sessionManager.getNumberOfSessionsByState(UiSessionState.ACTIVE))
            .description("Current number of active UI sessions.")
            .tag(ACTIVITY_STATE_TAG, UiSessionState.ACTIVE.toString().toLowerCase())
            .register(registry);

        Gauge.builder("teamapps.uisession", () -> sessionManager.getNumberOfSessionsByState(UiSessionState.NEARLY_INACTIVE))
            .description("Current number of nearly inactive UI sessions.")
            .tag(ACTIVITY_STATE_TAG, UiSessionState.NEARLY_INACTIVE.toString().toLowerCase())
            .register(registry);

        Gauge.builder("teamapps.uisession", () -> sessionManager.getNumberOfSessionsByState(UiSessionState.INACTIVE))
            .description("Current number of inactive UI sessions.")
            .tag(ACTIVITY_STATE_TAG, UiSessionState.INACTIVE.toString().toLowerCase())
            .register(registry);

        Gauge.builder("teamapps.uisession.commandbuffers.size", sessionManager, TeamAppsSessionManager::getBufferedCommandsCount)
            .description("Current number of all commands in all command buffers.")
            .register(registry);

        Gauge.builder("teamapps.uisession.commandbuffers.unconsumed", sessionManager, TeamAppsSessionManager::getUnconsumedCommandsCount)
            .description("Current number of commands in command buffers that are queued waiting to be consumed.")
            .register(registry);
    }
}
