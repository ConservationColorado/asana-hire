CREATE TABLE IF NOT EXISTS "User"
(
    ID       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NAME     VARCHAR(255) NOT NULL,
    EMAIL    VARCHAR(255) NOT NULL,
    PICTURE  VARCHAR(255) NOT NULL,
    PROVIDER VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "Job"
(
    ID                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    TITLE                  VARCHAR(255) NOT NULL,
    APPLICATION_PROJECT_ID VARCHAR(255) NOT NULL,
    INTERVIEW_PROJECT_ID   VARCHAR(255) NOT NULL
);
