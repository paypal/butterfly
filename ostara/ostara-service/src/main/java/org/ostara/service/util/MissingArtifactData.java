package org.ostara.service.util;

import java.util.List;

import org.ebaysf.ostara.telemetry.mongodb.ArtifactsData;
import org.ebaysf.ostara.telemetry.mongodb.TelemetryData;

public class MissingArtifactData {

	private ArtifactsData artifact;
	
	private List<TelemetryData> telemetryList;

	public ArtifactsData getArtifact() {
		return artifact;
	}

	public void setArtifact(ArtifactsData artifact) {
		this.artifact = artifact;
	}

	public List<TelemetryData> getTelemetryList() {
		return telemetryList;
	}

	public void setTelemetryList(List<TelemetryData> telemetryList) {
		this.telemetryList = telemetryList;
	}

	@Override
  public String toString() {
	  return "MissingArtifactData [artifact=" + artifact + ", telemetryList="
	      + telemetryList + "]";
  }
}
