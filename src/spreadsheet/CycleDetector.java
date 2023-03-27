package spreadsheet;

import common.api.BasicSpreadsheet;
import common.api.CellLocation;
import java.util.HashSet;
import java.util.Set;

/** Detects dependency cycles. */
public class CycleDetector {
  /**
   * Constructs a new cycle detector.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param spreadsheet The parent spreadsheet, used for resolving cell locations.
   */

  private final BasicSpreadsheet spreadsheet;
  private final Set<CellLocation> visited;

  CycleDetector(BasicSpreadsheet spreadsheet) {
    this.spreadsheet = spreadsheet;
    visited = new HashSet<>();
  }

  /**
   * Checks for a cycle in the spreadsheet, starting at a particular cell.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   *
   * @param start The cell location where cycle detection should start.
   * @return Whether a cycle was detected in the dependency graph starting at the given cell.
   */
  public boolean hasCycleFrom(CellLocation start) {
    // solution using recursive DFS

    Set<CellLocation> dependencies = new HashSet<>();
    spreadsheet.findCellReferences(start, dependencies);
    visited.add(start);

    for (CellLocation dependency : dependencies) {
      if (visited.contains(dependency)
          || !visited.contains(dependency) && hasCycleFrom(dependency)) {
        return true;
      }
    }
    visited.remove(start);
    return false;
  }
}
