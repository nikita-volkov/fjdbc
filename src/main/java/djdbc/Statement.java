package djdbc;

import java.sql.*;

/**
 * Statement specification.
 */
public interface Statement<params, result> {

  result run(ExtendedConnection connection, params params) throws SQLException;

  class Unpreparable<result> implements Statement<Void, result> {
    final String sql;
    final Decoder<result> decoder;

    public Unpreparable(String sql, Decoder<result> decoder) {
      this.sql = sql;
      this.decoder = decoder;
    }

    @Override
    public result run(ExtendedConnection connection, Void aVoid) throws SQLException {
      final java.sql.Statement statement = connection.jdbcConnection.createStatement();
      try {
        decoder.execute(statement, sql);
        return decoder.decode(statement);
      } finally {
        statement.close();
      }
    }
  }

  class Preparable<params, result> implements Statement<params, result> {
    final String sql;
    final Encoder<params> encoder;
    final Decoder<result> decoder;

    public Preparable(String sql, Encoder<params> encoder, Decoder<result> decoder) {
      this.sql = sql;
      this.encoder = encoder;
      this.decoder = decoder;
    }

    @Override
    public result run(ExtendedConnection connection, params params) throws SQLException {
      PreparedStatement preparedStatement =
        connection.preparedStatementCache.get(sql, decoder.getPreparedStatementFactory(connection.jdbcConnection));
      encoder.encodeParams(preparedStatement, params);
      preparedStatement.execute();
      return decoder.decode(preparedStatement);
    }
  }

}

