import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.ArrayList;

public class PointSET {

  private TreeSet<Point2D> _pointSet;

  // construct an empty set of points
  public PointSET() { _pointSet = new TreeSet<>(Point2D.X_ORDER); }

  // is the set empty?
  public boolean isEmpty() { return _pointSet.isEmpty(); }

  // number of points in the set
  public int size() { return _pointSet.size(); }

  // add the point to the set (if it is not already in the set)
  public void insert(Point2D point_) { _pointSet.add(point_); }

  // does the set contain point p?
  public boolean contains(Point2D point_) { return _pointSet.contains(point_); }

  // draw all points to standard draw
  public void draw()
  {
    for (Point2D point : _pointSet)
    {
      point.draw();
    }
  }

  // all points that are inside the rectangle (or on the boundary)
  public Iterable<Point2D> range(RectHV rect_)
  {
    ArrayList<Point2D> setOfPoints = new ArrayList<>();

    Point2D minXPoint = new Point2D(rect_.xmin(), rect_.ymin());
    Point2D maxXPoint = new Point2D(rect_.xmax(), rect_.ymin());
    double yMin = rect_.ymin();
    double yMax = rect_.ymax();

    NavigableSet<Point2D> subSet = _pointSet.subSet(minXPoint, true, maxXPoint, true);
    for (Point2D point : subSet)
    {
      if ((point.y() >= yMin) && (point.y() <= yMax))
      {
        setOfPoints.add(point);
      }
    }

    return setOfPoints;
  }

  // a nearest neighbor in the set to point p; null if the set is empty
  public Point2D nearest(Point2D point_)
  {
    Point2D nearestPoint = null;
    Double  nearestDistance = Double.POSITIVE_INFINITY;
    for (Point2D point : _pointSet)
    {
      Double distance  = point.distanceTo(point_);
      if (distance < nearestDistance)
      {
        nearestDistance = distance;
        nearestPoint = point;
      }
    }

    return nearestPoint;
  }

  // unit testing of the methods (optional)
  public static void main(String[] args)
  {

  }
}

