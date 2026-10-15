package ExtremePoints.ItemPairs.ItemPositionPair;

import ExtremePoints.Item.*;
import Geometry.Vector3.*;

/**
 * A simple record class that stores two objects, an item and a vector3
 */

public record ItemPositionPair(Item item, Vector3 vector3) {};