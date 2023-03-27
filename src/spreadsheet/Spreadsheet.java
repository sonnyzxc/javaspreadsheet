package spreadsheet;

import static spreadsheet.Parser.parse;

import common.api.BasicSpreadsheet;
import common.api.CellLocation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Spreadsheet implements BasicSpreadsheet {
  //
  // start replacing
  //
  /**
   * Construct an empty spreadsheet.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   */

  private final Map<CellLocation, Cell> cellMap;

  Spreadsheet() {
    cellMap = new HashMap<>();
  }

  /**
   * Parse and evaluate an expression, using the spreadsheet as a context.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   */
  public double evaluateExpression(String expression)
      throws InvalidSyntaxException {
    return parse(expression).evaluate(this);
  }

  @Override
  public double getCellValue(CellLocation location) {
    cellMap.putIfAbsent(location, new Cell(this, location));
    return cellMap.get(location).getValue();
  }

  public void setCellExpression(CellLocation location, String input)
      throws InvalidSyntaxException {

    cellMap.putIfAbsent(location, new Cell(this, location));
    cellMap.get(location).setExpression(input);

    CycleDetector cycleDetector = new CycleDetector(this);
    if (cycleDetector.hasCycleFrom(location)) {
      return;
    }
    cellMap.get(location).recalculate();
  }

  //
  // end replacing
  //

  @Override
  public String getCellExpression(CellLocation location) {
    cellMap.putIfAbsent(location, new Cell(this, location));
    return cellMap.get(location).getExpression();
  }

  @Override
  public String getCellDisplay(CellLocation location) {
    cellMap.putIfAbsent(location, new Cell(this, location));
    return cellMap.get(location).toString();
  }

  @Override
  public void addDependency(CellLocation dependent, CellLocation dependency) {
    Cell dependencyCell = new Cell(this, dependency);
    cellMap.putIfAbsent(dependency, dependencyCell);
    cellMap.get(dependency).addDependent(dependent);
  }

  @Override
  public void removeDependency(CellLocation dependent, CellLocation dependency) {
    Cell dependencyCell = new Cell(this, dependency);
    cellMap.putIfAbsent(dependency, dependencyCell);
    cellMap.get(dependency).removeDependent(dependent);
  }

  @Override
  public void recalculate(CellLocation location) {
    if (cellMap.containsKey(location)) {
      cellMap.get(location).recalculate();
    }
  }

  @Override
  public void findCellReferences(CellLocation subject, Set<CellLocation> target) {
    cellMap.putIfAbsent(subject, new Cell(this, subject));
    cellMap.get(subject).findCellReferences(target);
  }
}
