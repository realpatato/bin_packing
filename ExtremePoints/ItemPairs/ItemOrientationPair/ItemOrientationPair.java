package ExtremePoints.ItemPairs.ItemOrientationPair;

import ExtremePoints.Item.*;
import Geometry.Vector3.*;

import java.util.ArrayList;

/**
 * A simple record class that stores two objects, an item and a list of vector3s
 */

public record ItemOrientationPair(Item item, ArrayList<Vector3> vector3s) {};