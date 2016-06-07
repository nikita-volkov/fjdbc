package djdbc.drivers;

import djdbc.Driver;

import java.sql.SQLException;

public final class H2Driver implements Driver {
  public final static H2Driver i = new H2Driver();
  private H2Driver() {}
  @Override
  public boolean detectTransactionConflict(SQLException exception) {
    return false;
  }
}
