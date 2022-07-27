package com.example;

public interface PhysicsInterface {
    final double acceleration = -0.049; // scaled to make the game more of a human playable pace

    double finalVelocity(double velocity, double time);

    //double finalPosition(double position, double velocity, double time);
}
