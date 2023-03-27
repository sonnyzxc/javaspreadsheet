package spreadsheet;

import static spreadsheet.Parser.parse;

import common.api.BasicSpreadsheet;
import common.api.CellLocation;
import common.api.Expression;
import java.util.HashSet;
import java.util.Set;

/**
 * A single cell in a spreadsheet, tracking the expression, value, and other parts of cell state.
 */
public class Cell {
  /**
   * Constructs a new cell.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param spreadsheet The parent spreadsheet,
   * @param location The location of this cell in the spreadsheet.
   */
  private final BasicSpreadsheet spreadsheet;
  private final CellLocation cellLocation;
  private double cellValue;
  private Expression cellExpression;
  private final Set<CellLocation> dependentCells;

  Cell(BasicSpreadsheet spreadsheet, CellLocation location) {
    this.spreadsheet = spreadsheet;
    cellLocation = location;
    cellValue = 0.0;
    cellExpression = null;
    dependentCells = new HashSet<>();
  }

  /**
   * Gets the cell's last calculated value.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @return the cell's value.
   */
  public double getValue() {
    return cellValue;
  }

  /**
   * Gets the cell's last stored expression, in string form.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @return a string that parses to an equivalent expression to that last stored in the cell; if no
   *     expression is stored, we return the empty string.
   */
  public String getExpression() {
    return cellExpression == null ? "" : cellExpression.toString();
  }

  /**
   * Sets the cell's expression from a string.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param input The string representing the new cell expression.
   * @throws InvalidSyntaxException if the string cannot be parsed.
   */
  public void setExpression(String input) throws InvalidSyntaxException {
    final Set<CellLocation> oldDependencies = new HashSet<>();
    findCellReferences(oldDependencies);
    oldDependencies.forEach(dependency -> spreadsheet.removeDependency(cellLocation, dependency));
    if (input.equals("")) {
      cellExpression = null;
    } else {
      cellExpression = parse(input);
      final Set<CellLocation> newDependencies = new HashSet<>();
      findCellReferences(newDependencies);
      newDependencies.forEach(dependency -> spreadsheet.addDependency(cellLocation, dependency));
    }
  }

  /**
   * @return a string representing the value, if any, of this cell.
   */
  @Override
  public String toString() {
    return cellExpression == null ? "" : Double.toString(getValue());
  }

  /**
   * Adds the given location to this cell's dependents.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param location the location to add.
   */
  public void addDependent(CellLocation location) {
    dependentCells.add(location);
  }

  /**
   * Adds the given location to this cell's dependents.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param location the location to add.
   */
  public void removeDependent(CellLocation location) {
    dependentCells.remove(location);
  }

  /**
   * Adds this cell's expression's references to a set.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param target The set that will receive the dependencies for this
   */
  public void findCellReferences(Set<CellLocation> target) {
    if (cellExpression != null) {
      cellExpression.findCellReferences(target);
    }
  }

  /**
   * Recalculates this cell's value based on its expression.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   */
  public void recalculate() {
    cellValue = cellExpression == null ? 0.0 : cellExpression.evaluate(spreadsheet);
    dependentCells.forEach(spreadsheet::recalculate);
  }
}
