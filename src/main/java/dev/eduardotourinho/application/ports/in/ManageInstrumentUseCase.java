package dev.eduardotourinho.application.ports.in;

public interface ManageInstrumentUseCase {

    void addInstrument(String isin, String description);

    void deleteInstrument(String isin);
}
