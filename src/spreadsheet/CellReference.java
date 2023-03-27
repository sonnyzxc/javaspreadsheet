package spreadsheet;

import common.api.CellLocation;
import common.api.EvaluationContext;
import common.api.Expression;
import java.util.Set;

public class CellReference implements Expression {

  private final CellLocation cell;

  public CellReference(CellLocation inputCell) {
    cell = inputCell;
  }


  @Override
  public double evaluate(EvaluationContext context) {
    return context.getCellValue(cell);
  }

  @Override
  public void findCellReferences(Set<CellLocation> dependencies) {
    dependencies.add(cell);
  }

  @Override
  public String toString() {
    return cell.toString();
  }
}
