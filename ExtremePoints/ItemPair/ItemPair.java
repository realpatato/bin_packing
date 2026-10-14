package ExtremePoints.ItemPair;

import ExtremePoints.Item.*;
import Geometry.Vector3.*;

/**
 * A simple record class that stores two objects, an item and a vector3 (typically orientation or position)
 */

public record ItemPair(Item item, Vector3 vector3) { };