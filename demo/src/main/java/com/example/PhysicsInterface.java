package com.example;

public interface PhysicsInterface {
    final double acceleration = -0.098; // scaled to make the game more of a human playable pace
    // couldn't figure out the correct configuration for acceleration to match the millis timeline

    double finalVelocity(double velocity, double time);
}
