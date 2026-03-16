CREATE TABLE instruments (
    id          UUID         NOT NULL,
    isin        VARCHAR(12)  NOT NULL,
    description VARCHAR(255),
    CONSTRAINT pk_instruments PRIMARY KEY (id),
    CONSTRAINT uq_instruments_isin UNIQUE (isin)
);

CREATE TABLE quotes (
    id            UUID             NOT NULL,
    instrument_id UUID             NOT NULL,
    price         DOUBLE PRECISION NOT NULL,
    timestamp     TIMESTAMP        NOT NULL,
    CONSTRAINT pk_quotes PRIMARY KEY (id),
    CONSTRAINT fk_quotes_instrument FOREIGN KEY (instrument_id) REFERENCES instruments (id) ON DELETE CASCADE
);
