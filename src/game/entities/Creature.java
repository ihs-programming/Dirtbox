package game.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import game.Sprite;
import game.Viewport;
import game.blocks.Block;
import game.blocks.BlockType;
import game.world.World;

public abstract class Creature extends Entity {
	private final float HEALTH_BAR_HEIGHT = .1f;
	private final float HEALTH_BAR_DISPLACEMENT = .1f;
	private final float DAMAGE_FADE_TIME = 250;
	protected int totalHealth = 20;
	protected int health;
	private float timeSinceLastHit;
	protected int numberOfJumps = 0;
	private float framesUnderWater = 0;
	private float timeTillDamageUnderWater = 7500f;
	private float timeSinceLastDrownDamage = 0f;
	private int drownDamage = 3;
	private float drownDamageRate = 1500f;

	public Creature(Sprite sprite, Vector2f pos, World w) {
		super(sprite, pos, w);
		accel.y = GRAVITY;
		health = totalHealth;
	}

	public void doHit(Entity aggressor, int damage) {
		Vector2f dist = getLocation().sub(aggressor.getLocation());

		if (dist.length() < 5) {
			vel.x += dist.normalise().scale(0.01f).x;
			health -= damage;
			timeSinceLastHit = 0f;
		}
	}

	public void doHit(int damage) {
		health -= damage;
		timeSinceLastHit = 0f;
	}

	@Override
	public boolean alive() {
		return health > 0;
	}

	@Override
	public void draw(Viewport vp) {
		super.draw(vp);
		Rectangle outline = sprite.getBoundingBox();
		Color damagedColor = new Color(1, 0, 0,
				(DAMAGE_FADE_TIME - timeSinceLastHit) / DAMAGE_FADE_TIME);
		vp.fill(outline, damagedColor);
		if (Viewport.DEBUG_MODE) {
			// create health bar
			Rectangle healthBarOutline = new Rectangle(0, 0, 1, 1);
			Rectangle healthBar = new Rectangle(0, 0, 1.0f * health / totalHealth, 1);
			Transform barTransform = new Transform(new float[] { getHitbox().getWidth(),
					0,
					pos.x, 0, HEALTH_BAR_HEIGHT, pos.y - HEALTH_BAR_DISPLACEMENT });

			vp.fill(healthBar.transform(barTransform), Color.red);
			vp.draw(healthBarOutline.transform(barTransform), Color.white);
		}
	}

	@Override
	public void update(World w, float frametime) {
		super.update(w, frametime);
		timeSinceLastHit += frametime;

		if (Viewport.globaltimer >= 1000) {

			if (isInWater()) {
				this.framesUnderWater += frametime;
				timeSinceLastDrownDamage += frametime;
				if (this.framesUnderWater >= this.timeTillDamageUnderWater
						&& timeSinceLastDrownDamage >= drownDamageRate) {
					timeSinceLastDrownDamage = 0f;
					doHit(drownDamage);
				}
			} else {
				this.framesUnderWater = 0;
			}
		}
	}

	private boolean isInWater() {
		Block testBlock = world.getBlock(World.getCoordinates(super.pos));
		return testBlock != null && testBlock.type == BlockType.WATER;
	}

	protected void jump(float jumpStrength, int jumplimit) {
		if (isInWater() || numberOfJumps < jumplimit) {
			vel.y = -jumpStrength;
			numberOfJumps++;
		}
	}

	@Override
	protected void falldamage() {
		if (numberOfJumps != 0) {
			numberOfJumps = 0;
		}
		if (vel.getY() > 0.03) {
			doHit((int) (vel.getY() * 300));
		}
	}
}
