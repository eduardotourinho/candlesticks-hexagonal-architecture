# Architecture

The service follows **Hexagonal Architecture** (Ports & Adapters). The application core defines use case interfaces (ports) that adapters implement or consume, keeping business logic free of infrastructure dependencies.

```mermaid
flowchart LR
    User(["User\n(REST)"])
    Partner(["Partner Service\n(WebSocket)"])

    subgraph in_rest ["REST adapter (in)"]
        Controller["CandlestickController"]
        Mapper["ResponseMapper"]
        ExHandler["GlobalExceptionHandler"]
    end

    subgraph in_ws ["WebSocket adapter (in)"]
        ISH["InstrumentStreamHandler"]
        QSH["QuotesStreamHandler"]
    end

    subgraph app ["Application"]
        subgraph ports_in ["Ports (in)"]
            FCUC(["FindCandlesticksUseCase"])
            MIUC(["ManageInstrumentUseCase"])
            MQUC(["ManageQuoteUseCase"])
        end

        subgraph services ["Services"]
            CS["CandlestickService"]
            CF["CandlestickFactory"]
            IM["InstrumentManager"]
        end

        subgraph ports_out ["Ports (out)"]
            IMP(["InstrumentManagerPort"])
            QMP(["QuoteManagerPort"])
            QFP(["QuoteFinderPort"])
        end
    end

    subgraph out_storage ["Storage adapter (out)"]
        ISM["InstrumentStorageManager"]
        H2[("H2\n(Embedded)")]
    end

    User -->|"GET /candlesticks?isin="| Controller
    Controller --> Mapper
    Controller --> FCUC
    FCUC --> CS
    CS --> CF
    CS --> QFP

    Partner -->|"ws /instruments"| ISH
    Partner -->|"ws /quotes"| QSH
    ISH --> MIUC
    QSH --> MQUC
    MIUC --> IM
    MQUC --> IM
    IM --> IMP
    IM --> QMP

    IMP --> ISM
    QMP --> ISM
    QFP --> ISM
    ISM --> H2
```
