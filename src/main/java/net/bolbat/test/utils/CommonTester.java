package net.bolbat.test.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Utility for common testing approaches, like exception's testing (mostly for code coverage).
 * 
 * @author Alexandr Bolbat
 */
public final class CommonTester {

	/**
	 * Default constructor with preventing instantiations of this class.
	 */
	private CommonTester() {
		throw new IllegalAccessError("Shouldn't be instantiated.");
	}

	/**
	 * Check is class protected from instantiation by default constructor.
	 * 
	 * @param clazz
	 *            type
	 */
	public static void checkNotInstantiableDefaultConstructor(final Class<?> clazz) {
		checkNotInstantiableDefaultConstructor(clazz, null);
	}

	/**
	 * Check is class protected from instantiation by default constructor.
	 * 
	 * @param clazz
	 *            type
	 * @param expectedException
	 *            expected exception while instantiation
	 */
	public static <T extends Throwable> void checkNotInstantiableDefaultConstructor(final Class<?> clazz, final Class<T> expectedException) {
		try {
			final Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			constructor.newInstance();
			throw new AssertionError(String.format("Shouldn't be possible type[%s] instantiation", clazz));
		} catch (final NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new AssertionError(String.format("Unexpected type[%s] instantiation problem ", clazz));
		} catch (final InvocationTargetException e) {
			if (expectedException != null && (e.getCause() == null || !e.getCause().getClass().equals(expectedException))) {
				final String format = "Unexpected type[%s] instantiation exception cause[%s], expected[%s]";
				final String message = String.format(format, clazz, e.getCause(), expectedException);
				throw new AssertionError(message);
			}
		}
	}

	/**
	 * Check exceptions instantiation.
	 * 
	 * @param clazz
	 *            exception class
	 */
	public static <T extends Exception> void checkExceptionInstantiation(final Class<T> clazz) {
		final String exceptionMessage = "Test exception";
		final Throwable exceptionCause = new IllegalArgumentException("Test cause");

		final Constructor<?>[] constructors = clazz.getConstructors();
		for (Constructor<?> constructor : constructors) {
			if (constructor.getParameterTypes().length == 0) {
				@SuppressWarnings("unchecked")
				final T instance = (T) instantiate(constructor);
				// message validation
				if (instance.getMessage() != null)
					throw new AssertionError("Exception[" + instance + "] message should be null.");
				// cause validation
				if (instance.getCause() != null)
					throw new AssertionError("Exception[" + instance + "] cause should be null.");
				continue;
			}

			if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0] == String.class) {
				@SuppressWarnings("unchecked")
				final T instance = (T) instantiate(constructor, exceptionMessage);
				// message validation
				if (instance.getMessage() == null)
					throw new AssertionError("Exception[" + instance + "] message shouldn't be null.");
				if (!exceptionMessage.equals(instance.getMessage()))
					throw new AssertionError("Exception[" + instance + "] message should be equal with [" + exceptionMessage + "].");
				// cause validation
				if (instance.getCause() != null)
					throw new AssertionError("Exception[" + instance + "] cause should be null.");
				continue;
			}
			if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0] == Throwable.class) {
				@SuppressWarnings("unchecked")
				final T instance = (T) instantiate(constructor, exceptionCause);
				// message validation
				if (instance.getMessage() == null)
					throw new AssertionError("Exception[" + instance + "] message shouldn't be null.");
				if (!exceptionCause.toString().equals(instance.getMessage()))
					throw new AssertionError("Exception[" + instance + "] message should be equal with [" + exceptionCause.toString() + "].");
				// cause validation
				if (instance.getCause() == null)
					throw new AssertionError("Exception[" + instance + "] cause shouldn't be null.");
				if (!exceptionCause.getMessage().equals(instance.getCause().getMessage()))
					throw new AssertionError("Exception[" + instance + "] cause message should be equal with [" + exceptionCause.getMessage() + "].");
				continue;
			}
			if (constructor.getParameterTypes().length == 2 && constructor.getParameterTypes()[0] == String.class
					&& constructor.getParameterTypes()[1] == Throwable.class) {
				@SuppressWarnings("unchecked")
				final T instance = (T) instantiate(constructor, exceptionMessage, exceptionCause);
				// message validation
				if (instance.getMessage() == null)
					throw new AssertionError("Exception[" + instance + "] message shouldn't be null.");
				if (!exceptionMessage.equals(instance.getMessage()))
					throw new AssertionError("Exception[" + instance + "] message should be equal with [" + exceptionMessage + "].");
				// cause validation
				if (instance.getCause() == null)
					throw new AssertionError("Exception[" + instance + "] cause shouldn't be null.");
				if (!exceptionCause.getMessage().equals(instance.getCause().getMessage()))
					throw new AssertionError("Exception[" + instance + "] cause message should be equal with [" + exceptionCause.getMessage() + "].");
				continue;
			}
		}
	}

	/**
	 * Instantiate class by given constructor and parameters.
	 * 
	 * @param constructor
	 *            class constructor
	 * @param parameters
	 *            constructor parameters
	 * @return instance
	 */
	private static <T> T instantiate(final Constructor<T> constructor, final Object... parameters) {
		if (constructor == null)
			throw new IllegalArgumentException("arg[constructor] is null");

		try {
			if (parameters == null || parameters.length == 0)
				return constructor.newInstance();

			return constructor.newInstance(parameters);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
