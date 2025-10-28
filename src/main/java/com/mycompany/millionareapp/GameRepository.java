/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;


import java.util.List;
import java.sql.*;
import com.mycompany.millionareapp.Question;
/**
 *
 * @author rupertguppy
 */

/**
 * GameRepository — Derby Embedded DB façade (single source of truth).
 *
 * PURPOSE
 *  - Own all DB access for questions, players, sessions, and lifeline audit.
 *  - Embedded only (no server). Zero UI/console code here.
 *
 * CONNECTION
 *  - JDBC URL pattern: "jdbc:derby:<dbPath>;create=true"  (e.g., "db/MillionaireDB")
 *  - getConnection(): open a new connection via DriverManager.
 *
 * SCHEMA (to be created in ensureSchema)
 *  - QUESTION(
 *      id BIGINT IDENTITY PK,
 *      stem VARCHAR(1024) NOT NULL,
 *      optA VARCHAR(512) NOT NULL,
 *      optB VARCHAR(512) NOT NULL,
 *      optC VARCHAR(512) NOT NULL,
 *      optD VARCHAR(512) NOT NULL,
 *      correct SMALLINT NOT NULL CHECK (correct BETWEEN 0 AND 3),
 *      category VARCHAR(128) NULL,
 *      difficulty SMALLINT NULL
 *    )
 *  - PLAYER(
 *      id BIGINT IDENTITY PK,
 *      name VARCHAR(128) NOT NULL UNIQUE
 *    )
 *  - GAME_SESSION(
 *      id BIGINT IDENTITY PK,
 *      player_id BIGINT NOT NULL REFERENCES PLAYER(id),
 *      winnings INTEGER DEFAULT 0,
 *      elapsed_seconds BIGINT DEFAULT 0,
 *      started_at TIMESTAMP NOT NULL,
 *      finished_at TIMESTAMP NULL
 *    )
 *  - LIFELINE_USE(
 *      id BIGINT IDENTITY PK,
 *      session_id BIGINT NOT NULL REFERENCES GAME_SESSION(id),
 *      name VARCHAR(32) NOT NULL,           // "50/50" or "REVEAL"
 *      question_id BIGINT NOT NULL REFERENCES QUESTION(id),
 *      used_at TIMESTAMP NOT NULL
 *    )
 *  - Indexing (for leaderboard): index on GAME_SESSION(winnings DESC, finished_at DESC)
 *
 * SEEDING (seedIfEmpty)
 *  - If QUESTION.count == 0, batch insert starter List<Question> (preserve file order).
 *  - Starter source likely from FileQuestionRepository.parseQuestions("data/questions.txt").
 *
 * PUBLIC API (method contracts to implement later — no logic here yet)
 *  - void ensureSchema()
 *  - void seedIfEmpty(List<Question> starter)
 *  - java.sql.Connection getConnection()
 *
 *  - List<Question> findAllQuestions()                     // ordered by id ASC
 *  - long ensurePlayer(String name)                        // fetch-or-insert, return id
 *  - long startSession(long playerId)                      // insert row, return session id
 *  - void finishSession(long sessionId, int winnings,
 *                       long elapsedSeconds, java.time.Instant finishedAt)
 *  - void recordLifelineUse(long sessionId, String lifelineName, long questionId)
 *  - List<Object[]> topSessions(int limit)                 // [playerName, winnings, finished_at]
 *
 * IMPLEMENTATION NOTES
 *  - Use prepared statements; try-with-resources; no static global Connection.
 *  - Keep each method small/clear for marking; throw RuntimeException on fatal SQL errors.
 *  - Typical gameplay must exercise ≥3 reads (e.g., questions, leaderboard) and ≥3 writes
 *    (startSession, finishSession, recordLifelineUse).
 *  - Keep calls short (GUI runs on EDT); heavy work can be batched where appropriate.
 */

public class GameRepository {
    private final String url;
    // constructor
    public GameRepository(String dbPath){
        this.url = "jdbc:derby:" + dbPath + ";create=true";
    }
    // this method gets the connection to the database
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to open Derby connection: " + url, e);
        }
    }
    // this method creates all the table and indexes if they are missing
    public void ensureSchema() {
        try (Connection cn = getConnection()) {
            cn.setAutoCommit(true);

            createTableIfMissing(cn, "PLAYER", """
                CREATE TABLE PLAYER (
                  ID BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                  NAME VARCHAR(128) NOT NULL UNIQUE,
                  CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

            createTableIfMissing(cn, "QUESTION", """
                CREATE TABLE QUESTION (
                  ID BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                  STEM VARCHAR(1024) NOT NULL,
                  OPTA VARCHAR(512) NOT NULL,
                  OPTB VARCHAR(512) NOT NULL,
                  OPTC VARCHAR(512) NOT NULL,
                  OPTD VARCHAR(512) NOT NULL,
                  CORRECT SMALLINT NOT NULL CHECK (CORRECT BETWEEN 0 AND 3),
                )
                """);

            createTableIfMissing(cn, "GAME_SESSION", """
                CREATE TABLE GAME_SESSION (
                  ID BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                  PLAYER_ID BIGINT NOT NULL REFERENCES PLAYER(ID),
                  WINNINGS INTEGER DEFAULT 0,
                  ELAPSED_SECONDS BIGINT DEFAULT 0,
                  STARTED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  FINISHED_AT TIMESTAMP
                )
                """);

            createTableIfMissing(cn, "LIFELINE_USE", """
                CREATE TABLE LIFELINE_USE (
                  ID BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                  SESSION_ID BIGINT NOT NULL REFERENCES GAME_SESSION(ID),
                  NAME VARCHAR(32) NOT NULL,
                  QUESTION_ID BIGINT NOT NULL REFERENCES QUESTION(ID),
                  USED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);

            // Simple indexes helpful for leaderboard queries
            createIndexIfMissing(cn, "IDX_SESSION_WINNINGS", "GAME_SESSION", "WINNINGS");
            createIndexIfMissing(cn, "IDX_SESSION_FINISHED_AT", "GAME_SESSION", "FINISHED_AT");

        } catch (SQLException e) {
            throw new IllegalStateException("Schema bootstrap failed", e);
        }
    }
    
    public int seedIfEmpty(List<Question> starter) {
        if (starter == null || starter.isEmpty()) {
            return 0;
        }

        try (Connection cn = getConnection()) {
            // Is table empty?
            int count;
            try (Statement st = cn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM QUESTION")) {
                rs.next();
                count = rs.getInt(1);
            }
            if (count > 0) {
                return 0;
            }

            String sql = """
            INSERT INTO QUESTION (STEM, OPTA, OPTB, OPTC, OPTD, CORRECT)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                for (Question q : starter) {
                    ps.setString(1, q.getQuestion());
                    ps.setString(2, q.getOption(0).toString());
                    ps.setString(3, q.getOption(1).toString());
                    ps.setString(4, q.getOption(2).toString());
                    ps.setString(5, q.getOption(3).toString());
                    ps.setInt(6, q.getCorrectAnswer());
                    ps.addBatch();
                }
                int[] res = ps.executeBatch();
                int inserted = 0;
                for (int r : res) {
                    if (r >= 0) {
                        inserted += r;
                    } else if (r == Statement.SUCCESS_NO_INFO) {
                        inserted += 1;
                    }
                }
                if (inserted == 0 && res.length == starter.size()) {
                    inserted = starter.size();
                }
                return inserted;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Seeding failed", e);
        }
    }
    
    // helper method to check if table exists
    private static boolean tableExists(Connection cn, String tableUpper) throws SQLException{
        DatabaseMetaData md = cn.getMetaData();
        try (ResultSet rs = md.getTables(null, null, tableUpper, new String[]{"TABLE"})) {
            return rs.next();
        }
    }
    
    // helper method to create a table if missing
    private static void createTableIfMissing(Connection cn, String tableUpper, String ddl) throws SQLException {
        if (!tableExists(cn, tableUpper)) {
            try (Statement st = cn.createStatement()) {
                st.executeUpdate(ddl);
            }
        }
    }
    
    // helper method to check if index exists
    private static boolean indexExists(Connection cn, String indexUpper, String tableUpper) throws SQLException {
        DatabaseMetaData md = cn.getMetaData();
        try (ResultSet rs = md.getIndexInfo(null, null, tableUpper, false, false)) {
            while (rs.next()) {
                String idx = rs.getString("INDEX_NAME");
                if (idx != null && idx.equalsIgnoreCase(indexUpper)) return true;
            }
            return false;
        }
    }
    // helper method to create a table index if missing
    private static void createIndexIfMissing(Connection cn, String indexUpper, String tableUpper, String colUpper) throws SQLException {
        if (!indexExists(cn, indexUpper, tableUpper)) {
            try (Statement st = cn.createStatement()) {
                st.executeUpdate("CREATE INDEX " + indexUpper + " ON " + tableUpper + " (" + colUpper + ")");
            }
        }
    }
    
    // ------- API to implement in later steps -------
    public List<Question> findAllQuestions() { throw new UnsupportedOperationException("TODO"); }
    public long ensurePlayer(String name) { throw new UnsupportedOperationException("TODO"); }
    public long startSession(long playerId) { throw new UnsupportedOperationException("TODO"); }
    public void finishSession(long sessionId, int winnings, long elapsedSeconds, java.time.Instant finishedAt) { throw new UnsupportedOperationException("TODO"); }
    public void recordLifelineUse(long sessionId, String lifelineName, long questionId) { throw new UnsupportedOperationException("TODO"); }
    public List<Object[]> topSessions(int limit) { throw new UnsupportedOperationException("TODO"); }
    
    
}
    

    
    
    
    
    
    

