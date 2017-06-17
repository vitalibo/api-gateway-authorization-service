package com.github.vitalibo.authorization.shared.core;

import java.util.function.BiConsumer;

public interface Rule<T> extends BiConsumer<T, ErrorState> {

}
