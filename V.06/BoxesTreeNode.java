// After applying the logic of the boxes within the scene (Boxes.java), this class will divide the triangles and put them in the boxes.
// Having a list of triangles (formally made in Model3D), the idea to divide the triangles is by doing it in halves recursively.
// The simplest and most effective way to divide the triangles is by the longest axis of the box where the triangles are.
// Of course, arranging the triangles through the center of their box on that axis, and cutting them in half.
// This tends to produce compact children boxes that overlap little with each other (this is the goal this class is meant to achieve).

// From the box [T1, T2, T3, T4] --> [T1, T2] [T3, T4]

// There are 3 recursion cases this class will manage and decide to keep the recursion or stop and finish:
// The cases depends on one single question: How many triangles are in the actual list?

// Case 1: There is 1 triangle -> children box with a triangle (only holds one triangle) -> stop dividing
// Case 2: There are 2 triangles -> children box with two triangles (holds both triangles with both left & right "hands") -> stop dividing
// Case 3: There are 3 or more triangles -> sort by longest axis, cut in half and ask again until it becomes case 1 or 2


// This is the logic of how is the travel through the tree when shooting a ray:

// Does the ray actually HIT this box?
// If NO: return NO HIT and DISCARD THE ENTIRE BRANCH OF THE BOX (the box and its children)
// If YES:
//      Is it a leaf / last-children?
//      If YES: try the intersection with the triangles  and return the closest one (the only one or the closest between both, depending the case).
//      IF  NO:
//            Ask both left and right children (box) if the ray hits them and recursively repeat asking for hit and if its a last-children.
//            At last, return the hit of the triangle of the last children (or the closest between the right/left triangle if is case 2).

// It is said that this method makes most branches to end in the first NO.

import java.util.ArrayList;
import java.util.List;

public class BoxesTreeNode {

    private final Boxes box; // This is the box that contains everything within the node
    private final BoxesTreeNode leftChild; // Left child (null if is a leaf / last-child)
    private final BoxesTreeNode rightChild; // Right child (null if is a leaf / last-child)
    private final List<TriangleIntersection> triangles; // it will only be populated in leafs / last-childrens

    // Limit of children for every last-children node: if there are this number of triangles or less, the node is a last-children directly
    // A child with 1-2 triangles is not worth continuing to divide
    private static final int LAST_CHILDREN_CHILD_LIMIT = 2;

    // Primary Builder: Receives the complete list of triangles from this node and recursively construct the subtree.
    public BoxesTreeNode(List<TriangleIntersection> triangles) {

        // LAST-CHILD CASE: few triangles, stop dividing
        if (triangles.size() <= LAST_CHILDREN_CHILD_LIMIT) {
            this.triangles = triangles;
            this.leftChild  = null; // A last-child cannot have a leftChild
            this.rightChild = null; // A last-child cannot have a rightChild
            // The box wraps all the triangles of this sheet
            this.box = buildBox(triangles);
            return;
        }

        // INTERNAL NODE CASE: There are enough triangles to make splitting (divide) worth it

        // Step 1: Calculate the box that surrounds all the triangles of this node
        // Must know how wide it is on each axis to choose which one to divide by (how wide is in X, Y & Z axis)
        Boxes totalBox = buildBox(triangles);

        // Step 2: Choose the wider axis
        // The widest axis is the one that most "separates" the triangles from each other
        // Dividing by it produces the two most compact daughter boxes possible
        double wideAxisX = totalBox.getMaxEdgeBox().getX() - totalBox.getMinEdgeBox().getX();
        double wideAxisY = totalBox.getMaxEdgeBox().getY() - totalBox.getMinEdgeBox().getY();
        double wideAxisZ = totalBox.getMaxEdgeBox().getZ() - totalBox.getMinEdgeBox().getZ();

        int widerAxis; // 0 = X, 1 = Y, 2 = Z
        if (wideAxisX >= wideAxisY && wideAxisX >= wideAxisZ) {widerAxis = 0;} // The widest axis is the Y axis
        else if (wideAxisY >= wideAxisZ) {widerAxis = 1;} // If the X axis is NOT the widest, compare between Y & Z axis
        else {widerAxis = 2;} // If Y axis is not wider than the Z axis, this one is the widest

        // Step 3: Arrange the triangles by the center of their box on the chosen axis
        // I use the center instead of the vertex because it is more representative of where the triangle is in space

        final int sortAxis = widerAxis; // Final variable to the lambda function

        // List of the triangles that now will be sorted triangle list
        List<TriangleIntersection> sortedTriangleList = new ArrayList<>(triangles); 

        sortedTriangleList.sort((a, b) -> {
            // centerA is the center of the box of triangle A on the chosen axis; centerB is the same for triangle B
            // The center of the box of a triangle on an axis is the average of the minimum and maximum edge of the box of that triangle on that axis
            // The sort is done by comparing the centers of the boxes of the triangles on the chosen axis, 
            // So the triangles are arranged from left to right (or bottom to top, or back to front, depending on the axis) in the sorted list
            double centerA = boxOf(a).getMinEdgeBox().getComponent(sortAxis) + boxOf(a).getMaxEdgeBox().getComponent(sortAxis);
            double centerB = boxOf(b).getMinEdgeBox().getComponent(sortAxis) + boxOf(b).getMaxEdgeBox().getComponent(sortAxis);
            // Returns a negative value if centerA < centerB, zero if they are equal, and a positive value if centerA > centerB
            return Double.compare(centerA, centerB);
            // It is not dividde by 2 to get the real center because the only thing that matters is the relative order, 
            // and dividing both by 2 does not change that order.
        });

        // Step 4: Cut the sorted triangle list list in half
        int halfTriangleList = sortedTriangleList.size() / 2;
        // The sorted triangle list, now split in two, is assigned in two lists (left and right) taking its half as reference
        List<TriangleIntersection> leftList  = sortedTriangleList.subList(0, halfTriangleList);
        List<TriangleIntersection> rightList = sortedTriangleList.subList(halfTriangleList, sortedTriangleList.size());

        // Step 5: Recursiveness  -> each half builds its own subtree
        // It does everything from the LAST-CHILD CASE to splitting its triangle list in halves
        // Both right and left childs of the first ones
        this.leftChild  = new BoxesTreeNode(new ArrayList<>(leftList));
        this.rightChild = new BoxesTreeNode(new ArrayList<>(rightList));

        // Step 6: The box of this internal node is the surrounding of their own two children
        // The totalBox calculated above will not be used because the surrounding box is slightly more accurate
        // by building from the leaves upwards, extra processes are saved.
        this.box = Boxes.surrounding(this.leftChild.box, this.rightChild.box);
        this.triangles = null; // Internal nodes do not store triangles directly
    }



    // This method goes through the tree with the ray shot and returns the closest intersection
    public Intersection intersect(Ray ray, double minDistanceT, double maxDistanceT) {

        // Quick discard question: does the ray even touch the box of this node?
        // If the ray doesn't even touches it, no triangle inside that box can be touched either.
        if (!box.intersectBox(ray, minDistanceT, maxDistanceT)) {
            return new Intersection(); // The box and its whole branch (children) are out of the question.
            // Precisely, this discard is what makes BoundingBoxes so optimal and recommended
        }

        // ASSUMING I AM A LAST-CHILD / LEAF: it has to be tested against real triangles
        if (triangles != null) { // If there are triangles in this node, it is a leaf, so it has to be tested against them
            Intersection closest = new Intersection(); // The closest hit is initialized as no hit, so any hit will be closer than it
            for (TriangleIntersection tri : triangles) { // For each triangle of this leaf:
                Intersection hit = tri.intersect(ray); // Test the intersection with the ray and get the hit (if there is)
                // It must ONLY save the CLOSEST HIT WITHIN THE FRUSTUM

                // The hit is valid if there is a collision and it is within the frustum (between nearPlane and farPlane)
                if (hit.isCollition_happened() && hit.getT() > minDistanceT && hit.getT() < maxDistanceT) {
                    // If there is a hit and it is closer than the closest hit found so far:
                    if (!closest.isCollition_happened() || hit.getT() < closest.getT()) {
                        closest = hit; // Update the closest hit
                    }
                }
            }
            return closest;
        }

        // ASSUMING I'M AN INTERNAL NODE: ask both children and return the closest hit
        // The hit is in the left if the left child had an intersection with a triangle within the frustum
        Intersection hitLeft  = leftChild.intersect(ray, minDistanceT, maxDistanceT);
        // The hit is in the right if the right child had an intersection with a triangle within the frustum
        Intersection hitRight = rightChild.intersect(ray, minDistanceT, maxDistanceT);

        // If both children have a hit, the closest is the chosen one
        if (hitLeft.isCollition_happened() && hitRight.isCollition_happened()) {
            // Asks which one between them is closer and returns it
            return hitLeft.getT() < hitRight.getT() ? hitLeft : hitRight;
        }
        // If both didn't hit --> If only one has a hit, return that one
        if (hitLeft.isCollition_happened()) {return hitLeft;}
        if (hitRight.isCollition_happened()) {return hitRight;}
        // If neither of them hit anything (obviously, within the frustum):
        return new Intersection(); // Return no hit
    }

    // Calculate the minimum box that surrounds a triangle
    // It is used both to organize and to build the last-children / leaf boxes
    private static Boxes boxOf(TriangleIntersection tri) {
        // It calculates the minimum and maximum edges of the box that contains a triangle by comparing the coordinates of its vertices
        // Minimum edges of the box that contains a triangle
        double minX = Math.min(tri.getV0().getX(), Math.min(tri.getV1().getX(), tri.getV2().getX()));
        double minY = Math.min(tri.getV0().getY(), Math.min(tri.getV1().getY(), tri.getV2().getY()));
        double minZ = Math.min(tri.getV0().getZ(), Math.min(tri.getV1().getZ(), tri.getV2().getZ()));
        // // Maximum edges of the box that contains a triangle
        double maxX = Math.max(tri.getV0().getX(), Math.max(tri.getV1().getX(), tri.getV2().getX()));
        double maxY = Math.max(tri.getV0().getY(), Math.max(tri.getV1().getY(), tri.getV2().getY()));
        double maxZ = Math.max(tri.getV0().getZ(), Math.max(tri.getV1().getZ(), tri.getV2().getZ()));
        return new Boxes(new Vector3D(minX, minY, minZ), new Vector3D(maxX, maxY, maxZ));
    }

    // Calculate the minimum box that surrounds an entire list of triangles
    // Start with the first triangle and it will be expanding with surrounding() with the rest of the triangles.
    // So, in the end, it will be the minimum box that contains all the triangles.

    // Hitting that box may asure that the ray hits at least one triangle of the list, 
    // So it is a good way to discard many triangles with one single question.
    private static Boxes buildBox(List<TriangleIntersection> triangles) {
        // Start with the box of the first triangle as the initial box
        Boxes result = boxOf(triangles.get(0));
        // Then, for each triangle, expand the box to include it by surrounding the current box with the box of that triangle
        for (int i = 1; i < triangles.size(); i++) {
            // The surrounding of the current box and the box of the next triangle is the new box that contains all the triangles up to that point
            result = Boxes.surrounding(result, boxOf(triangles.get(i)));
        }
        // Returns the final box that contains all the triangles of the list
        return result;
    }
}