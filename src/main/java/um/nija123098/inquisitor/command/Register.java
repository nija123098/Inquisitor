package um.nija123098.inquisitor.command;

import um.nija123098.inquisitor.context.Rank;
import um.nija123098.inquisitor.context.Suspicion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Made by nija123098 on 11/7/2016
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface Register {
    /**
     * This is the full length name of the method,
     * but defaults if a natural to the method name,
     * else to the class name, then the method name
     *
     * @return the name used to call the method
     */
    String name() default "";
    String help() default "";
    /**
     * Indicates a stand alone command
     * @return whether or not the command has no other associated commands
     */
    boolean natural() default false;
    /**
     * Indicates if this command is the base for other commands,
     * exe... run for run program1, run program2, ect...
     * @return if the command is a default command for other commands
     */
    boolean defaul() default false;
    Rank rank() default Rank.BOT;
    boolean startup() default false;
    boolean shutdown() default false;
    boolean guild() default false;
    boolean hidden() default false;
    float suspicious() default 0;
    Suspicion suspicion() default Suspicion.RADICAL;
    /**
     * Returns true if it accepts args
     * @return If the command is invoked if there are args
     */
    boolean args() default true;
    boolean override() default false;
}
