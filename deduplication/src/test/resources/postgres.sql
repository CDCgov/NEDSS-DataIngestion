-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS algorithm_id_seq;

-- Table Definition
CREATE TABLE "public"."algorithm" (
    "id" int4 NOT NULL DEFAULT nextval('algorithm_id_seq'::regclass),
    "is_default" bool NOT NULL,
    "label" varchar(255) NOT NULL,
    "description" text,
    "include_multiple_matches" bool NOT NULL,
    "belongingness_ratio_lower_bound" float8 NOT NULL,
    "belongingness_ratio_upper_bound" float8 NOT NULL,
    PRIMARY KEY ("id")
);

-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS algorithm_pass_id_seq;

-- Table Definition
CREATE TABLE "public"."algorithm_pass" (
    "id" int4 NOT NULL DEFAULT nextval('algorithm_pass_id_seq'::regclass),
    "algorithm_id" int4 NOT NULL,
    "blocking_keys" json NOT NULL,
    "evaluators" json NOT NULL,
    "rule" varchar(255) NOT NULL,
    "kwargs" json NOT NULL,
    PRIMARY KEY ("id")
);

-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS mpi_blocking_value_id_seq;

-- Table Definition
CREATE TABLE "public"."mpi_blocking_value" (
    "id" int8 NOT NULL DEFAULT nextval('mpi_blocking_value_id_seq'::regclass),
    "patient_id" int8 NOT NULL,
    "blockingkey" int2 NOT NULL,
    "value" varchar(20) NOT NULL,
    PRIMARY KEY ("id")
);

-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS mpi_patient_id_seq;

-- Table Definition
CREATE TABLE "public"."mpi_patient" (
    "id" int8 NOT NULL DEFAULT nextval('mpi_patient_id_seq'::regclass),
    "person_id" int8,
    "data" json NOT NULL,
    "external_patient_id" varchar(255),
    "external_person_id" varchar(255),
    "external_person_source" varchar(100),
    "reference_id" uuid NOT NULL,
    PRIMARY KEY ("id")
);

-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS mpi_person_id_seq;

-- Table Definition
CREATE TABLE "public"."mpi_person" (
    "id" int8 NOT NULL DEFAULT nextval('mpi_person_id_seq'::regclass),
    "reference_id" uuid NOT NULL,
    PRIMARY KEY ("id")
);

INSERT INTO "public"."algorithm" ("id", "is_default", "label", "description", "include_multiple_matches", "belongingness_ratio_lower_bound", "belongingness_ratio_upper_bound") VALUES
(1, 't', 'dibbs-basic', 'The DIBBs Default Algorithm. Based on field experimentation and statistical analysis, this deterministic two-pass algorithm combines geographical and personal information to maximize linkage quality while minimizing false positives', 't', 0.75, 0.9),
(2, 'f', 'dibbs-enhanced', 'The DIBBs Log-Odds Algorithm. This optional algorithm uses statistical correction to adjust the links between incoming records and previously processed patients (it does so by taking advantage of the fact that some fields are more informative than others—e.g., two records matching on MRN is stronger evidence that they should be linked than if the records matched on zip code). It can be used if additional granularity in matching links is desired. However, while the DIBBs Log-Odds Algorithm can create higher-quality links, it is dependent on statistical updating and pre-calculated population analysis, which requires some work on the part of the user. For those cases where additional precision or stronger matching criteria are required, the Log-Odds algorithm is detailed below.', 't', 0.75, 0.9);

INSERT INTO "public"."algorithm_pass" ("id", "algorithm_id", "blocking_keys", "evaluators", "rule", "kwargs") VALUES
(1, 1, '["BIRTHDATE", "MRN", "SEX"]', '[{"feature": "FIRST_NAME", "func": "func:recordlinker.linking.matchers.compare_fuzzy_match"}, {"feature": "LAST_NAME", "func": "func:recordlinker.linking.matchers.compare_match_all"}]', 'func:recordlinker.linking.matchers.rule_match', '{"thresholds": {"FIRST_NAME": 0.9, "LAST_NAME": 0.9, "BIRTHDATE": 0.95, "ADDRESS": 0.9, "CITY": 0.92, "ZIP": 0.95}}'),
(2, 1, '["ZIP", "FIRST_NAME", "LAST_NAME", "SEX"]', '[{"feature": "ADDRESS", "func": "func:recordlinker.linking.matchers.compare_fuzzy_match"}, {"feature": "BIRTHDATE", "func": "func:recordlinker.linking.matchers.compare_match_all"}]', 'func:recordlinker.linking.matchers.rule_match', '{"thresholds": {"FIRST_NAME": 0.9, "LAST_NAME": 0.9, "BIRTHDATE": 0.95, "ADDRESS": 0.9, "CITY": 0.92, "ZIP": 0.95}}'),
(3, 2, '["BIRTHDATE", "MRN", "SEX"]', '[{"feature": "FIRST_NAME", "func": "func:recordlinker.linking.matchers.compare_probabilistic_fuzzy_match"}, {"feature": "LAST_NAME", "func": "func:recordlinker.linking.matchers.compare_probabilistic_fuzzy_match"}]', 'func:recordlinker.linking.matchers.rule_probabilistic_match', '{"similarity_measure": "JaroWinkler", "thresholds": {"FIRST_NAME": 0.9, "LAST_NAME": 0.9, "BIRTHDATE": 0.95, "ADDRESS": 0.9, "CITY": 0.92, "ZIP": 0.95}, "true_match_threshold": 12.2, "log_odds": {"ADDRESS": 8.438284928858774, "BIRTHDATE": 10.126641103800338, "CITY": 2.438553006137189, "FIRST_NAME": 6.849475906891162, "LAST_NAME": 6.350720397426025, "MRN": 0.3051262572525359, "SEX": 0.7510419059643679, "STATE": 0.022376768992488694, "ZIP": 4.975031471124867}}'),
(4, 2, '["ZIP", "FIRST_NAME", "LAST_NAME", "SEX"]', '[{"feature": "ADDRESS", "func": "func:recordlinker.linking.matchers.compare_probabilistic_fuzzy_match"}, {"feature": "BIRTHDATE", "func": "func:recordlinker.linking.matchers.compare_probabilistic_fuzzy_match"}]', 'func:recordlinker.linking.matchers.rule_probabilistic_match', '{"similarity_measure": "JaroWinkler", "thresholds": {"FIRST_NAME": 0.9, "LAST_NAME": 0.9, "BIRTHDATE": 0.95, "ADDRESS": 0.9, "CITY": 0.92, "ZIP": 0.95}, "true_match_threshold": 17.0, "log_odds": {"ADDRESS": 8.438284928858774, "BIRTHDATE": 10.126641103800338, "CITY": 2.438553006137189, "FIRST_NAME": 6.849475906891162, "LAST_NAME": 6.350720397426025, "MRN": 0.3051262572525359, "SEX": 0.7510419059643679, "STATE": 0.022376768992488694, "ZIP": 4.975031471124867}}');



-- Indices
CREATE UNIQUE INDEX algorithm_label_key ON public.algorithm USING btree (label);
CREATE INDEX ix_algorithm_is_default ON public.algorithm USING btree (is_default);
ALTER TABLE "public"."algorithm_pass" ADD FOREIGN KEY ("algorithm_id") REFERENCES "public"."algorithm"("id") ON DELETE CASCADE;
ALTER TABLE "public"."mpi_blocking_value" ADD FOREIGN KEY ("patient_id") REFERENCES "public"."mpi_patient"("id");


-- Indices
CREATE INDEX idx_blocking_value_patient_key_value ON public.mpi_blocking_value USING btree (patient_id, blockingkey, value);
ALTER TABLE "public"."mpi_patient" ADD FOREIGN KEY ("person_id") REFERENCES "public"."mpi_person"("id");


-- Indices
CREATE UNIQUE INDEX ix_mpi_patient_reference_id ON public.mpi_patient USING btree (reference_id);


-- Indices
CREATE UNIQUE INDEX ix_mpi_person_reference_id ON public.mpi_person USING btree (reference_id);
