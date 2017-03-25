package com.android.animations;

import android.opengl.GLES20;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.joml.Matrix4f;

import org.jbox2d.dynamics.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;


public class Game {

    private MainActivity mParentActivity = null;
    private TextView mResetTextView;
    private int mWidth;
    private int mHeight;
    private Matrix4f mView = new Matrix4f();
    private Texture mBallTexture;
    private Sprite mBallSprite;
    private World mB2World;
    private Vector4f mDrawWhite = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    enum ObjectType {
        Floor,
        Ball
    }


    Map<Integer, Body> mCirclesMap = new HashMap<>();
    private double mLastTouchX;
    private double mLastTouchY;
    private static final int PUSH_STRENGTH = 20000;


    Game(MainActivity parent) {
        mParentActivity = parent;
        mResetTextView = (TextView) mParentActivity.findViewById(R.id.frame_rate);
        mB2World = new World(new Vec2(0.0f, 0f));
    }

    private void reset() {
        for (Body b = mB2World.getBodyList(); b != null; b = b.getNext()) mB2World.destroyBody(b);
        createHorizontalWall(new Vec2(-50f, 1f));
        createHorizontalWall(new Vec2(-50f, 20f));
        createVerticalWall(new Vec2(-30f, 0f));
        createVerticalWall(new Vec2(75f, 0f));

        createBallsOnTheLeftSide();
        createBallsOnTheRightSide();
    }

    private void createBallsOnTheLeftSide() {
        createBall(new Vec2(-3.0f, 10.0f), null, 1);
        createBall(new Vec2(-5.0f, 10.0f), null, 2);
        createBall(new Vec2(-7.0f, 10.0f), null, 3);
        createBall(new Vec2(-9.0f, 10.0f), null, 4);
        createBall(new Vec2(-11.0f, 10.0f), null, 5);


        createBall(new Vec2(-3.0f, 11.5f), null, 6);
        createBall(new Vec2(-5.0f, 11.5f), null, 7);
        createBall(new Vec2(-7.0f, 11.5f), null, 8);
        createBall(new Vec2(-9.0f, 11.5f), null, 9);
        createBall(new Vec2(-11.0f, 11.5f), null, 10);
    }

    private void createBallsOnTheRightSide() {
        createBall(new Vec2(16.0f, 10.0f), null, 11);
        createBall(new Vec2(18.0f, 10.0f), null, 12);
        createBall(new Vec2(20.0f, 10.0f), null, 13);
        createBall(new Vec2(22.0f, 10.0f), null, 14);
        createBall(new Vec2(24.0f, 10.0f), null, 15);

        createBall(new Vec2(16.0f, 11.5f), null, 16);
        createBall(new Vec2(18.0f, 11.5f), null, 17);
        createBall(new Vec2(20.0f, 11.5f), null, 18);
        createBall(new Vec2(22.0f, 11.5f), null, 19);
        createBall(new Vec2(24.0f, 11.5f), null, 20);


    }

    private void createBall(Vec2 position, @Nullable Vec2 velocity, int id) {
        Vec2 v = velocity == null ? new Vec2() : velocity;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position = position;
        bodyDef.gravityScale = 0.0f;
        bodyDef.linearDamping = 2.0f;
        bodyDef.userData = (Object) ObjectType.Ball;
        bodyDef.type = BodyType.DYNAMIC;

        CircleShape shape = new CircleShape();
        shape.setRadius(1.3f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.userData = null;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0f;
        fixtureDef.density = 1.0f;

        Body body = mB2World.createBody(bodyDef);
        body.createFixture(fixtureDef);
        mCirclesMap.put(id, body);
    }

    private void createHorizontalWall(Vec2 position) {
        BodyDef bodyDef = new BodyDef();

        bodyDef.position = position;
        bodyDef.userData = (Object) ObjectType.Floor;
        bodyDef.type = BodyType.KINEMATIC;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(125.0f, 0.05f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        Body body = mB2World.createBody(bodyDef);
        body.createFixture(fixtureDef);
    }


    private void createVerticalWall(Vec2 position) {
        BodyDef bodyDef = new BodyDef();

        bodyDef.position = position;
        bodyDef.userData = (Object) ObjectType.Floor;
        bodyDef.type = BodyType.KINEMATIC;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.05f, 25f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        Body body = mB2World.createBody(bodyDef);
        body.createFixture(fixtureDef);
    }

    public void Init() {

        Square.InitSquare();

        // Enable 2D Textures
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // Enable Culling
        //GLES20.glFrontFace(GLES20.GL_CCW);
        //GLES20.glEnable(GLES20.GL_CULL_FACE);
        //GLES20.glCullFace(GLES20.GL_BACK);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        mBallTexture = new Texture(mParentActivity.getApplicationContext(), R.drawable.ball1);
        mBallSprite = new Sprite(mBallTexture);

        GLES20.glClearColor(235f / 255.0f, 235f / 255.0f, 255f / 255.0f, 255f / 255.0f);

        mResetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mParentActivity.getSemaphore().acquire(1);
                    reset();
                } catch (Exception e) {

                } finally {
                    mParentActivity.getSemaphore().release(1);
                }
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        reset();
    }

    public void Update(float delta) {

        mB2World.step(delta, 20, 20);

        mB2World.clearForces();
        for (Map.Entry<Integer, Body> entry : mCirclesMap.entrySet()) {

            Vec2 circlePosition = entry.getValue().getWorldCenter();
            Vec2 centerPosition = new Vec2(7.0f, 10.0f);
            Vec2 centertDistance = new Vec2(0, 0);
            centertDistance.addLocal(circlePosition);
            centertDistance.subLocal(centerPosition);

            float finalDistance = centertDistance.length();
            centertDistance.negateLocal();

            centertDistance.mulLocal((float) (1.0 / (finalDistance * 0.030)));


            double distanceFromCenter = distanceBetweenPoints(centerPosition, entry.getValue().getPosition());
            float linearDamping = (float) (distanceFromCenter > 5 ? 2 : 2 + (5 - distanceFromCenter));
            entry.getValue().setLinearDamping(linearDamping);

            if (entry.getKey() == 6) {
                if (finalDistance < 5.0 && finalDistance > 3.0) {
                    Vec2 impulse = new Vec2(0, (float) (1.0 / (finalDistance * 0.8)));
                    entry.getValue().applyLinearImpulse(impulse, entry.getValue().getWorldCenter());
                }
            }

            if (entry.getKey() == 1) {
                if (finalDistance < 5.0 && finalDistance > 3.0) {
                    Vec2 impulse = new Vec2(0, (float) (-1.0 / (finalDistance * 0.8)));
                    entry.getValue().applyLinearImpulse(impulse, entry.getValue().getWorldCenter());
                }
            }

            if (entry.getKey() == 16) {
                if (finalDistance < 4.0 && finalDistance > 2.0) {
                    Vec2 impulse = new Vec2(0, (float) (1.0 / (finalDistance * 0.8)));
                    entry.getValue().applyLinearImpulse(impulse, entry.getValue().getWorldCenter());
                }
            }


            if (entry.getKey() == 11) {
                if (finalDistance < 4.0 && finalDistance > 2.0) {
                    Vec2 impulse = new Vec2(0, (float) (-1.0 / (finalDistance * 0.8)));
                    entry.getValue().applyLinearImpulse(impulse, entry.getValue().getWorldCenter());
                }
            }

            entry.getValue().applyForce(centertDistance, entry.getValue().getWorldCenter());
        }
    }


    private double distanceBetweenPoints(Vec2 point1, Vec2 point2) {
        return Math.hypot(point2.x - point1.x, point2.y - point1.y);
    }

    void draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        int num_objects = mB2World.getBodyCount();
        if (num_objects >= 0) {
            Body body = mB2World.getBodyList();
            for (int i = 0; i < num_objects; i++) {
                Square.mColor = mDrawWhite;
                switch ((ObjectType) body.getUserData()) {
                    case Ball:
                        Vec2 position = body.getWorldCenter();
                        // 0.5/1.05=0.4761 Now 1.2/0.4761=2.52
                        mBallSprite.draw(position, body.getAngle(), 2.73f / 100.0f, mView);
                        break;
                }
                body = body.getNext();
            }
        }
    }

    public void setUpUi() {
        mResetTextView.setText("reset");
    }


    public void onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case ACTION_DOWN:
                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                break;
            case ACTION_MOVE:
                double x = event.getX();
                double y = event.getY();
                double dx = x - mLastTouchX;
                double dy = y - mLastTouchY;
                double b = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
                dx = (b == 0) ? 0 : (dx / b);
                dy = (b == 0) ? 0 : (dy / b);
                if (dx == 0 && dy == 0) {
                    return;
                }
                for (Map.Entry<Integer, Body> entry : mCirclesMap.entrySet()) {
                    Vec2 direction = new Vec2((float) (PUSH_STRENGTH * dx), (float) (-PUSH_STRENGTH * dy));
                    entry.getValue().applyForce(direction, entry.getValue().getWorldCenter());
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            case ACTION_UP:
                break;

        }
    }

    public void SetSize(int width, int height) {
        mWidth = width;
        mHeight = height;

        final float height_ratio = ((float) height) / ((float) width);
        final float base_units = 13f;
        final float pixels_per_unit = 100.0f;
        float virtual_width = base_units;
        float virtual_height = virtual_width * height_ratio;


        mView = new Matrix4f().ortho(0, virtual_width, 0, virtual_height, 1, -1);
        //.orthoM(View,0,0,Width,0,Height,1,-1);
    }
}