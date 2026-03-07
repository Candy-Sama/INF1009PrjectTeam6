package com.team6.spacesim.components;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.team6.spacesim.ecs.Component;

public class CompositeShapeComponent implements Component {
    private List<SubShape> shapes;
    
    public CompositeShapeComponent() {
        this.shapes = new ArrayList<>();
    }
    
    // === METHODS TO MANAGE THE LIST OF SHAPES ===
    
    public List<SubShape> getShapes() {
        return shapes;
    }
    
    public void addShape(SubShape shape) {
        shapes.add(shape);
    }
    
    public void removeShape(SubShape shape) {
        shapes.remove(shape);
    }
    
    public void clearShapes() {
        shapes.clear();
    }
    
    public static class SubShape {
        
        public enum ShapeType {
            RECTANGLE,  // 4-sided box
            CIRCLE,     // Round shape
            LINE,       // Straight line between 2 points
            TRIANGLE,   // 3-sided shape
            POLYGON     // Many-sided shape
        }
        
        
        private ShapeType type;      
        private Color color;         
        private boolean filled;      // Solid or outline?
        private float offsetX;       
        private float offsetY;       
        private float[] dimensions;  // Size data (flexible array for different shapes)
        

        public SubShape(float offsetX, float offsetY, float width, float height, Color color, boolean filled) {
            this.type = ShapeType.RECTANGLE;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.color = color;
            this.filled = filled;
            this.dimensions = new float[]{width, height};  // Store width and height
        }
        
        public static SubShape createCircle(float offsetX, float offsetY, float radius, Color color, boolean filled) {
            SubShape shape = new SubShape();  // Empty object
            shape.type = ShapeType.CIRCLE;
            shape.offsetX = offsetX;
            shape.offsetY = offsetY;
            shape.color = color;
            shape.filled = filled;
            shape.dimensions = new float[]{radius};  // Just one number: radius
            return shape;
        }
        
        public static SubShape createLine(float x1, float y1, float x2, float y2, Color color) {
            SubShape shape = new SubShape();
            shape.type = ShapeType.LINE;
            shape.offsetX = 0;
            shape.offsetY = 0;
            shape.color = color;
            shape.filled = false;  // Lines can't be "filled", only drawn
            shape.dimensions = new float[]{x1, y1, x2, y2};  // Start and end points
            return shape;
        }
        
        public static SubShape createTriangle(float x1, float y1, float x2, float y2, 
                                              float x3, float y3, Color color, boolean filled) {
            SubShape shape = new SubShape();
            shape.type = ShapeType.TRIANGLE;
            shape.offsetX = 0;
            shape.offsetY = 0;
            shape.color = color;
            shape.filled = filled;
            shape.dimensions = new float[]{x1, y1, x2, y2, x3, y3};  // 3 points (6 numbers)
            return shape;
        }
        
        public static SubShape createPolygon(float[] vertices, Color color, boolean filled) {
            SubShape shape = new SubShape();
            shape.type = ShapeType.POLYGON;
            shape.offsetX = 0;
            shape.offsetY = 0;
            shape.color = color;
            shape.filled = filled;
            shape.dimensions = vertices.clone();  // Copy the array
            return shape;
        }
        
        private SubShape() {}
        
        // === GETTERS - Allow reading the shape's properties ===
        
        public ShapeType getType() {
            return type;
        }
        
        public Color getColor() {
            return color;
        }
        
        public boolean isFilled() {
            return filled;
        }
        
        public float getOffsetX() {
            return offsetX;
        }
        
        public float getOffsetY() {
            return offsetY;
        }
        
        public float[] getDimensions() {
            return dimensions;
        }
        
        // === SETTERS - Allow changing the shape after creation ===
        
        public void setColor(Color color) {
            this.color = color;
        }
        
        public void setFilled(boolean filled) {
            this.filled = filled;
        }
        
        public void setOffsetX(float offsetX) {
            this.offsetX = offsetX;
        }
        
        public void setOffsetY(float offsetY) {
            this.offsetY = offsetY;
        }
        
    }
}
