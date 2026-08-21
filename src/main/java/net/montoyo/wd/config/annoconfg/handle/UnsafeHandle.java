package net.montoyo.wd.config.annoconfg.handle;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Simple static-field read/write helper.
 *
 * The original implementation used sun.misc.Unsafe (theUnsafe via reflection),
 * which throws InaccessibleObjectException on Java 21 because the jdk.unsupported
 * module is not opened by NeoForge -> the mod constructor failed -> "broken mod state".
 *
 * All config fields are public static, so plain Field.get/set work fine.
 */
public class UnsafeHandle {
	private final Field field;
	private final Object relative;

	private final Consumer<Object> uploader;
	private final Supplier<Object> getter;

	public UnsafeHandle(Field f) {
		this(null, f);
	}

	public UnsafeHandle(Object relative, Field f) {
		this.field = f;
		this.relative = relative;
		uploader = (v) -> {
			try {
				field.set(relative, v);
			} catch (Throwable t) {
				throw new RuntimeException("AnnoCFG: Failed to write field " + f.getName(), t);
			}
		};
		getter = () -> {
			try {
				return field.get(relative);
			} catch (Throwable t) {
				throw new RuntimeException("AnnoCFG: Failed to read field " + f.getName(), t);
			}
		};
	}

	public void set(Object o) {
		uploader.accept(o);
	}

	public Object get() {
		return getter.get();
	}
}
