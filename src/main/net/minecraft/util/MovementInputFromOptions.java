package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;
import satellite.Client;
import satellite.event.listeners.EventPlayerInput;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
    	EventPlayerInput e = new EventPlayerInput(
	    			this.gameSettings.keyBindForward.isKeyDown(),
	    			this.gameSettings.keyBindBack.isKeyDown(),
	    			this.gameSettings.keyBindLeft.isKeyDown(),
	    			this.gameSettings.keyBindRight.isKeyDown(),
	    			this.gameSettings.keyBindJump.isKeyDown(),
	    			this.gameSettings.keyBindSneak.isKeyDown()
    			);
    	Client.onEvent(e);
    	
        this.moveStrafe = 0.0F;
        this.field_192832_b = 0.0F;

        if (e.isForward())
        {
            ++this.field_192832_b;
            this.forwardKeyDown = true;
        }
        else
        {
            this.forwardKeyDown = false;
        }

        if (e.isBack())
        {
            --this.field_192832_b;
            this.backKeyDown = true;
        }
        else
        {
            this.backKeyDown = false;
        }

        if (e.isLeft())
        {
            ++this.moveStrafe;
            this.leftKeyDown = true;
        }
        else
        {
            this.leftKeyDown = false;
        }

        if (e.isRight())
        {
            --this.moveStrafe;
            this.rightKeyDown = true;
        }
        else
        {
            this.rightKeyDown = false;
        }

        this.jump = e.isJump();
        this.sneak = e.isSneak();

        if (this.sneak)
        {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
            this.field_192832_b = (float)((double)this.field_192832_b * 0.3D);
        }
    }
}
