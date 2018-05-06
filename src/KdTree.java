import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.util.ArrayList;

public class KdTree {

  private Node _root = null;
  private int _size = 0;

  private class Node implements Comparable<Point2D>
  {
    Point2D _point;
    boolean _compareByY;

    Node   _rightNode = null;
    Node   _leftNode = null;
    RectHV _area;
    RectHV _leftRect;
    RectHV _rightRect;

    Node(Point2D point_, boolean compareByY_, RectHV area_)
    {
      this._point = point_;
      this._compareByY = compareByY_;
      this._area = area_;

      // determining left side
      if (this._compareByY)
      {
        _leftRect =  new RectHV(_area.xmin(), _area.ymin(), _area.xmax(), _point.y()); // yMax changed
      }
      else // compareByX
      {
        _leftRect = new RectHV(_area.xmin(), _area.ymin(), _point.x(), _area.ymax()); // xMax changed
      }

      // determining right side
      if (this._compareByY)
      {
        _rightRect = new RectHV(_area.xmin(), _point.y(), _area.xmax(), _area.ymax()); // yMin changed
      }
      else // compareByX
      {
        _rightRect = new RectHV(_point.x(), _area.ymin(), _area.xmax(), _area.ymax()); // xMin changed
      }
    }

    @Override
    public int compareTo(Point2D other_)
    {
      if (this._compareByY)
      {
        int comp = Double.compare(this._point.y(), other_.y());
        return ((comp == 0) ? Double.compare(this._point.x(), other_.x()) : comp);
      }
      else
      {
        int comp = Double.compare(this._point.x(), other_.x());
        return ((comp == 0) ? Double.compare(this._point.y(), other_.y()) : comp);
      }
    }

    public RectHV getLeftRect() { return _leftRect; }

    public RectHV getRightRect() { return _rightRect; }

    public boolean rectsIntersact(RectHV other_)
    {
      return this._area.intersects(other_);
    }

    public void draw()
    {
      StdDraw.setPenRadius(0.001);
      if (_compareByY)
      {

        StdDraw.setPenColor(Color.BLUE);
        StdDraw.line(_area.xmin(), _point.y(), _area.xmax(), _point.y());
        StdDraw.setPenColor();
      }
      else
      {
        StdDraw.setPenColor(Color.RED);
        StdDraw.line(_point.x(), _area.ymin(), _point.x(), _area.ymax());
        StdDraw.setPenColor();
      }
      StdDraw.setPenRadius(0.015);
      _point.draw();
      StdDraw.setPenRadius();
    }
  }

  // construct an empty set of points
  public KdTree() {}

  // is the set empty?
  public boolean isEmpty() { return _root == null;}

  // number of points in the set
  public int size() { return _size; }

  private Node insert(Node node_, boolean compareByY_, Point2D point_, RectHV rect_)
  {
    if (node_ == null)
    {
      this._size++;
      return new Node(point_, compareByY_, rect_);
    }

    int comp = (-1 * node_.compareTo(point_));

    if (comp < 0)      node_._leftNode  = insert(node_._leftNode,  !node_._compareByY, point_, node_.getLeftRect());
    else if (comp > 0) node_._rightNode = insert(node_._rightNode, !node_._compareByY, point_, node_.getRightRect());
    else               node_._point = point_;

    return node_;
  }

  // add the point to the set (if it is not already in the set)
  public void insert(Point2D point_)
  {
    if (point_ == null)
    {
      throw new java.lang.IllegalArgumentException();
    }
    _root = insert(_root, false, point_, new RectHV(0,0, 1, 1));
  }

  // does the set contain point p?
  public boolean contains(Point2D point_)
  {
    if (point_ == null)
    {
      throw new java.lang.IllegalArgumentException();
    }

    Node currentNode = this._root;
    while(currentNode != null)
    {
      int comp = (-1 * currentNode.compareTo(point_));
      if      (comp < 0) currentNode = currentNode._leftNode;
      else if (comp > 0) currentNode = currentNode._rightNode;
      else               return true;
    }

    return false;
  }

  private void draw(Node node_)
  {
    if (node_ == null) return;

    node_.draw();

    if (node_._rightNode != null) draw(node_._rightNode);
    if (node_._leftNode != null) draw(node_._leftNode);
  }

  // draw all points to standard draw
  public void draw() { draw(this._root); }

  private void range(Node node_, RectHV rect_, ArrayList<Point2D> points_)
  {
    if ((node_ == null) || (!node_.rectsIntersact(rect_))) return;
    if (rect_.contains(node_._point)) points_.add(node_._point);

    if (node_._leftRect.intersects(rect_))
    {
      range(node_._leftNode, rect_, points_);
    }

    if (node_._rightRect.intersects(rect_))
    {
      range(node_._rightNode, rect_, points_);
    }
  }

  // all points that are inside the rectangle (or on the boundary)
  public Iterable<Point2D> range(RectHV rect_)
  {
    if (rect_ == null)
    {
      throw new java.lang.IllegalArgumentException();
    }

    ArrayList<Point2D> points = new ArrayList<>();

    if (_root == null) return points;

    if (!this._root.rectsIntersact(rect_)) return points;

    range(_root, rect_, points);

    return points;
  }

  private Point2D nearest(Node node_, Point2D nearestPoint_, Point2D givenPoint_)
  {
    if (node_ == null) return nearestPoint_;

    double currentNearestDistance = nearestPoint_.distanceTo(givenPoint_);
    if (currentNearestDistance <= node_._area.distanceTo(givenPoint_)) return nearestPoint_;

    double distanceFromThisNode = node_._point.distanceTo(givenPoint_);
    nearestPoint_ = ((distanceFromThisNode < currentNearestDistance) ? node_._point : nearestPoint_);

    if ((node_._leftNode == null) && (node_._rightNode == null))
    {
      return nearestPoint_;
    }

    if (node_._rightNode != null)
    {
      nearestPoint_ = nearest(node_._rightNode, nearestPoint_, givenPoint_);
    }

    if (node_._leftNode != null)
    {
      nearestPoint_ = nearest(node_._leftNode, nearestPoint_, givenPoint_);
    }

    return nearestPoint_;
  }

  // a nearest neighbor in the set to point p; null if the set is empty
  public Point2D nearest(Point2D point_)
  {
    if (point_ == null)
    {
      throw new java.lang.IllegalArgumentException();
    }

    if (_root == null) return null;

    Point2D nearestPoint = _root._point;
    return nearest(_root, nearestPoint, point_);
  }

  // unit testing of the methods (optional)
  public static void main(String[] args)
  {

  }
}

