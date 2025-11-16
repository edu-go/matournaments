package com.ma.torneos.ui;

public class RegistrationView {
    private final Long registrationId;
    private final Long competitorId;
    private final String competitorName;
    private final String status; // PENDING / CONFIRMED / WITHDRAWN

    public RegistrationView(Long registrationId, Long competitorId,
                            String competitorName, String status) {
        this.registrationId = registrationId;
        this.competitorId = competitorId;
        this.competitorName = competitorName;
        this.status = status;
    }

    public Long getRegistrationId() { return registrationId; }
    public Long getCompetitorId() { return competitorId; }
    public String getCompetitorName() { return competitorName; }
    public String getStatus() { return status; }
}
