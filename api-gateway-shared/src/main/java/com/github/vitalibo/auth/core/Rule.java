package com.github.vitalibo.auth.core;

import java.util.function.BiConsumer;

public interface Rule<T> extends BiConsumer<T, ErrorState> {

}
