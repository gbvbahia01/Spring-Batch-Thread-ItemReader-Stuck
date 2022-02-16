package br.com.gbvbahia.threads.monitor.dto;

public enum BatchModeController {

  INSTANCE(BatchItemReaderMode.RETURN_NULL);

  private BatchItemReaderMode batchMode;

  private BatchModeController(BatchItemReaderMode batchMode) {
    this.batchMode = batchMode;
  }

  public BatchItemReaderMode getBatchMode() {
    return batchMode;
  }

  public void setBatchMode(BatchItemReaderMode batchMode) {
    this.batchMode = batchMode;
  }

}
