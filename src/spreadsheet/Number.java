package spreadsheet;

import common.api.CellLocation;
import common.api.EvaluationContext;
import common.api.Expression;
import java.util.Set;

public class Number implements Expression {

  private final double value;

  public Number(double num) {
    value = num;
  }

  @Override
  public double evaluate(EvaluationContext context) {
    return value;
  }

  @Override
  public void findCellReferences(Set<CellLocation> dependencies) {
  }

  @Override
  public String toString() {
    return Double.toString(value);
  }
}
