package com.blockified.alariaejmc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Proper quicksand, unlike Oobleck: it sheds depth more slowly than it
 * gains it, so wading out is a real struggle rather than a matter of
 * keeping your pace up. Struggling upward still helps - jumping counts as
 * movement, same as everywhere else (see QuicksandMotion).
 *
 * Depth scales with the column. A stack of three swallows nearly three
 * blocks rather than stopping you on the top one, and since the sink is
 * measured against the top of that column it stays consistent no matter
 * which block in the stack reports the collision.
 *
 * Collision is a shallow lip rather than a full box so entities can enter
 * at all; that is what makes the block passable from the sides above the
 * lip, the same as Oobleck and Mud Bog.
 */
public class BogSurfaceBlock extends Block {
	private static final VoxelShape COLLISION = VoxelShapes.cuboid(0, 0, 0, 1, 0.25, 1);
	private static final VoxelShape OUTLINE = VoxelShapes.fullCube();

	private static final int MAX_COLUMN = 8;
	/*Both rates are low, which is the "very slow to get out" part - a full
	  column takes seconds of sustained struggling. Rise still edges out
	  sink, because it has to: see QuicksandMotion.Tuning.*/
	private static final QuicksandMotion.Tuning TUNING =
			new QuicksandMotion.Tuning(0.035, 0.045, 0.3f, 0.1f, 4);

	public BogSurfaceBlock(Settings settings) {
		super(settings);
	}

	@Override
	public float getVelocityMultiplier() {
		return 0.3f;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return COLLISION;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);

		/*Walk to the top of this bog column, then measure how deep it runs.
		  Anchoring on the column top keeps the sink continuous however far
		  down the entity has already gone.*/
		BlockPos top = pos;
		for (int i = 0; i < MAX_COLUMN; i++) {
			if (!(world.getBlockState(top.up()).getBlock() instanceof BogSurfaceBlock)) {
				break;
			}
			top = top.up();
		}

		int layers = 1;
		BlockPos cursor = top;
		while (layers < MAX_COLUMN
				&& world.getBlockState(cursor.down()).getBlock() instanceof BogSurfaceBlock) {
			cursor = cursor.down();
			layers++;
		}

		/*Stop just shy of the bottom so nothing drops out the underside.*/
		QuicksandMotion.apply(world, entity, top.getY() + 1.0, layers - 0.2, TUNING);
	}
}
