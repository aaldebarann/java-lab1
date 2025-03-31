package org.example.lab1;

public record PlayerState(String name, int score, int shots, boolean isReady,
                          boolean isShooting, ArrowState arrow) {
}
