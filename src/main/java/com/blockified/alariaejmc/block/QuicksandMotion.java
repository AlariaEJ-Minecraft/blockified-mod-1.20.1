package com.blockified.alariaejmc.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Shared sink/rise handling for the quicksand-style blocks (Oobleck and
 * Bog Block), so the two cannot drift apart on the rules that matter:
 * standing still sinks, moving climbs out, and struggling upward counts
 * as moving.
 *
 * That last point is the whole reason this is one implementation. Judging
 * movement on horizontal speed alone scores a jump - which is nearly all
 * vertical - as standing still, so jumping drove the player deeper
 * instead of helping. Blocks tune the rates; none of them get to redefine
 * that.
 */
public final class QuicksandMotion {
	/**
	 * riseRate MUST stay above sinkRate. A jump spends roughly half its
	 * airtime climbing and half falling, so if rising shed less than
	 * falling gains, every jump would net downward - the exact thing this
	 * class exists to prevent. Slow escapes come from a low riseRate in
	 * absolute terms, never from letting it drop under sinkRate.
	 *
	 * @param sinkRate          depth gained per tick while still
	 * @param riseRate          depth shed per tick while moving; keep above
	 *                          sinkRate, lower for a slower escape
	 * @param stickyMultiplier  horizontal damping near the surface
	 * @param sunkMultiplier    horizontal damping once past half depth
	 * @param slownessAmplifier extra Slowness applied once past half depth
	 */
	public record Tuning(double sinkRate, double riseRate, float stickyMultiplier,
			float sunkMultiplier, int slownessAmplifier) {
	}

	private static final double MOVING_THRESHOLD = 0.02;
	private static final int FORGET_AFTER_TICKS = 200;
	private static final int PRUNE_WHEN_LARGER_THAN = 128;

	private static final class Sinking {
		double depth;
		long lastTick;
	}

	private static final Map<UUID, Sinking> sinking = new HashMap<>();

	private QuicksandMotion() {
	}

	/**
	 * @param surfaceY world Y the entity rides at when not sunk at all
	 * @param maxDepth how far below that it can go
	 */
	public static void apply(World world, Entity entity, double surfaceY, double maxDepth, Tuning tuning) {
		if (world.isClient || !(entity instanceof LivingEntity living)) {
			return;
		}

		long now = world.getTime();
		UUID id = living.getUuid();
		Sinking sink = sinking.get(id);

		/*Callers fire this once per overlapping block, so straddling a
		  boundary would otherwise sink and dampen two to four times in one
		  tick. Only the first block each tick gets to act.*/
		if (sink != null && sink.lastTick == now) {
			return;
		}
		if (sink == null) {
			sink = new Sinking();
			sinking.put(id, sink);
			prune(now);
		}
		sink.lastTick = now;

		Vec3d velocity = living.getVelocity();
		boolean climbing = velocity.horizontalLength() > MOVING_THRESHOLD
				|| velocity.y > MOVING_THRESHOLD;

		sink.depth = climbing
				? Math.max(0.0, sink.depth - tuning.riseRate())
				: Math.min(maxDepth, sink.depth + tuning.sinkRate());

		/*Held at depth 0 rather than dropped: removing the entry here would
		  let another overlapping block re-process the same tick.*/
		double top = surfaceY - sink.depth;
		if (living.getY() < top) {
			living.setPosition(living.getX(), top, living.getZ());
		}

		boolean sunk = sink.depth > maxDepth * 0.5;
		float multiplier = sunk ? tuning.sunkMultiplier() : tuning.stickyMultiplier();
		living.setVelocity(velocity.multiply(multiplier, velocity.y < 0 ? 0.7 : 1.0, multiplier));

		if (sunk) {
			living.addStatusEffect(new StatusEffectInstance(
					StatusEffects.SLOWNESS, 20, tuning.slownessAmplifier(), true, false));
		}
	}

	/*Entities that wander off while still sunk would otherwise sit in the
	  map forever.*/
	private static void prune(long now) {
		if (sinking.size() <= PRUNE_WHEN_LARGER_THAN) {
			return;
		}
		Iterator<Map.Entry<UUID, Sinking>> it = sinking.entrySet().iterator();
		while (it.hasNext()) {
			if (now - it.next().getValue().lastTick > FORGET_AFTER_TICKS) {
				it.remove();
			}
		}
	}
}
