/********************************************************************
    Copyright (c) 2000-2008 Steven E. Hugg.

    This file is part of FLCore.

    FLCore is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FLCore is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FLCore.  If not, see <http://www.gnu.org/licenses/>.
*********************************************************************/
package com.fasterlight.glout;

import java.awt.*;

import javax.media.opengl.GL;

import com.fasterlight.spif.*;
import com.fasterlight.util.StringUtil;
import com.fasterlight.vecmath.Vector2f;

/**
  * A label. Prints text. Can also edit the thing.
  * todo: this is too complicated, layout() is nastee
  * todo: get rid of hard-coded shaders
  * todo: can't edit w/o 'minchars' -- sets size to 0,0
  * todo: handle 'null' text
  * todo: handle substrings out of range
  */
public class GLOLabel
extends GLOComponent
{
	/**
	  * The current text string.
	  * While editing, this may not be the same as what is in the
	  * linked property.
	  */
	protected String text = "";
	/**
	  * The size of the current string -- used for text alignment,
	  * computing the minium size & more.
	  */
	protected Dimension textsize = new Dimension(0,0);
	/**
	  * Optional -- can set a fixed # of characters displayed
	  */
	protected int minchars=0;
	/**
	  * Horizontal alignment.
	  */
	protected int halign = ALIGN_LEFT;

	/**
	  * Can we edit?
	  */
	protected boolean editable;
	/**
	  * The first & last selection point.
	  * selfirst <= sellast, and both are >= 0 and <= text.length()
	  */
	protected int selfirst=-1, sellast;
	/**
	  * The character # that the cursor is at.
	  */
	protected int cursorpos;
	/**
	  * Before we start editing, we store the old text here
	  * in case we want to cancel.
	  */
	protected String preEditText;

	protected PropertyEvaluator prop_text = null;

	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_CENTER = 1;
	public static final int ALIGN_RIGHT = 2;

	public static int blinkrate = 250;

	/**
	  * The label that's printed when a property is not found.
	  */
	public static String PROP_NOT_FOUND = "";

	//

	public GLOLabel()
	{
	}

	public GLOLabel(String name)
	{
		setText(name);
	}

	public GLOLabel(String name, int alignment)
	{
		setText(name);
		setAlignment(alignment);
	}

	public GLOLabel(int minchars)
	{
		setMinChars(minchars);
	}

	/**
	  * Sets the minimum # of characters displayed.
	  */
	public void setMinChars(int n)
	{
		this.minchars = n;
		updateTextSize();
	}

	public int getMinChars()
	{
		return minchars;
	}

	/**
	  * Sets whether or not it's editable
	  */
	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}

	public boolean isEditable()
	{
		return editable;
	}

	public boolean getEditable()
	{
		return editable;
	}

	public void selectAll()
	{
		setSelection(0, text.length());
	}

	public int getSelFirst()
	{
		return selfirst;
	}

	public int getSelLast()
	{
		return sellast;
	}

	public int getCursorPos()
	{
		return cursorpos;
	}

	public void setSelection(int first, int last)
	{
		first = Math.max(0, first);
		if (text != null)
			last = Math.min(text.length(), last);
		if (first > last)
			first = last;
		this.selfirst = first;
		this.sellast = last;
		this.cursorpos = last;
	}

	public void cutSelection()
	{
		int l = text.length();
		if (selfirst >= 0 && sellast <= l)
		{
			setSelection(selfirst, sellast);
			setText( text.substring(0,selfirst) + text.substring(sellast) );
			setSelection(selfirst, selfirst);
		}
	}

	/**
	  * layout() must be called *after* adding to a window
	  * hierarchy which inclues a GLOContext, because we have
	  * to know the font size to know how to size this.
	  * Ain't life a beeatch?
	  */
	public void layout()
	{
		updateTextSize();
		setSize(textsize);
	}

	/**
	  * Sets the internal "textsize" variable, which determines
	  * what the size of the component will be, and thus affects
	  * culling of the component.
	  */
	private void updateTextSize()
	{
		GLOShader shader = getTextShader();
		textsize.height = shader.ysize;
		String s = getText();
		if (s != null)
		{
			int nchars = GLFontServer.countPrintable(s);
			textsize.width = shader.xsize*nchars;
		} else {
			textsize.width = 0;
			textsize.height = 0;
		}
	}

	/**
	  * Sets the attached property of this text.
	  */
	protected void setTextProperty(String text)
	{
		if (prop_text != null)
		{
			setForPropertyKey(prop_text, text);
		}
	}

	/**
	  * Sets the text without setting the attached property.
	  */
	public void setText(String text)
	{
		this.text = text;
		updateTextSize();
		// if we've not set the width or height, set it automagically
		if (getWidth() == 0 && getHeight() == 0)
			setSize(textsize);
	}

	public String getText()
	{
		return text;
	}

	protected String convertToString(Object o)
	{
		return PropertyUtil.toString(o);
	}

	public String getPropertyForText()
	{
		return getKey(prop_text);
	}

	public void setPropertyForText(String s)
	{
		this.prop_text = new PropertyEvaluator(s);
	}


	public void setAlignment(int a)
	{
		this.halign = a;
	}

	public int getAlignment()
	{
		return halign;
	}

	public Dimension getMinimumSize()
	{
		Dimension d = new Dimension(textsize.width, textsize.height);
      if (minchars > 0)
      {
      	int w = getTextShader().xsize*minchars;
      	if (w > d.width)
      		d.width = w;
      }
      return d;
	}

	/**
	  * Override for making the text of this component computable.
	  * If null, do nothing (default)
	  * This is called at render time.
	  */
	protected String computeText()
	{
		if (editable && ctx.getFocused() == this)
			return null; // if we are editing, do not change text
		if (prop_text != null)
		{
			Object o = getForPropertyKey(prop_text);
			if (o != null)
				return convertToString(o);
			else
				return text;
		}
		return null;
	}

	protected GLOShader getTextShader()
	{
		if (editable)
			return (ctx.getFocused() == this) ? getShader("editable") : getShader("editing");
		else
			return getShader("text");
	}

	protected GLFontServer setupFontServer()
	{
		GLOContext ctx = getContext();
		setShader(getTextShader());
		return ctx.getFontServer();
	}

	public void render(GLOContext ctx)
	{
		String str = computeText();
		if (str == null)
			str = this.text;
		if (str == null || str.length() == 0)
			return;

		if (!str.equals(text))
		{
			setText(str);
		}

		Point o = getOrigin();
		GL gl = ctx.getGL();

		GLFontServer fs = setupFontServer();

		boolean editmode = (editable && ctx.getFocused() == this && selfirst >= 0);

		float xpos = o.x;
		float width = textsize.width;
		if (!editmode && minchars > 0)
			width = Math.min(width, minchars*fs.getTextWidth());
		if (!editmode)
		{
			if (halign == ALIGN_CENTER)
				xpos += (w1-width)/2;
			else if (halign == ALIGN_RIGHT)
				xpos += w1-width;
		}

		// todo: valign?

		if (editmode)
		{
			fs.drawText(str.substring(0, selfirst), xpos, o.y+textsize.height);
			Vector2f dim = fs.getTextSize();
			setShader("seltext");
			fs.setTextSize(dim.x, dim.y);
			fs.drawText(str.substring(selfirst, sellast));
			setShader("text");
			fs.drawText(str.substring(sellast));
		} else {
			if (minchars > 0)
				str = StringUtil.truncateNicely(str, minchars, "...");
			fs.drawText(str, xpos, o.y+textsize.height);
		}

		// draw cursor
		if (editable && ctx.getFocused() == this)
		{
			setShader("selected");
			if (cursorpos >= 0 && cursorpos <= str.length()) {
				boolean blink = (ctx.getFrameStartMillis() % blinkrate) < (blinkrate/2);
				if (blink)
				{
					// draw the cursor
					float x = o.x+fs.getTextWidth()*cursorpos;
					drawBox(ctx, x, o.y-2, 4, textsize.height+2);
				}
			}
		}
	}

	public boolean typeChar(char ch)
	{
		cutSelection();
		setText( text.substring(0, cursorpos) + ch + text.substring(cursorpos) );
		setSelection(cursorpos+1, cursorpos+1);
		notifyDataChanged();
		return true;
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (editable)
		{
			if (event instanceof GLOFocusEvent)
			{
				GLOFocusEvent focev = (GLOFocusEvent)event;
				if (focev.isGained())
				{
					preEditText = getText();
					selectAll();
				} else {
					if (text != null)
						setTextProperty(text);
				}
				return true;
			}
			else if (event instanceof GLOKeyEvent)
			{
				GLOKeyEvent keyev = (GLOKeyEvent)event;
				if (keyev.pressed)
				{
					switch (keyev.keycode)
					{
						case GLOKeyEvent.VK_ENTER:
							event.getContext().requestFocus(null);
							notifyDataChanged();
							return true;
						case GLOKeyEvent.VK_ESCAPE:
							setText( preEditText );
							event.getContext().requestFocus(null);
							return true;
						case GLOKeyEvent.VK_LEFT:
							// cursor left
							setSelection(sellast-1, sellast-1);
							break;
						case GLOKeyEvent.VK_RIGHT:
							// cursor right
							setSelection(sellast+1, sellast+1);
							break;
						case GLOKeyEvent.VK_HOME:
							setSelection(0, 0);
							break;
						case GLOKeyEvent.VK_END:
							setSelection(text.length(), text.length());
							break;
						case GLOKeyEvent.VK_BACK_SPACE:
							cutSelection();
							if (cursorpos > 0)
							{
								setText( text.substring(0, cursorpos-1) + text.substring(cursorpos) );
								setSelection(cursorpos-1, cursorpos-1);
							}
							notifyDataChanged();
							return true;
						case GLOKeyEvent.VK_DELETE:
							cutSelection();
							if (cursorpos < text.length())
							{
								setText( text.substring(0, cursorpos) + text.substring(cursorpos+1) );
								setSelection(cursorpos, cursorpos);
							}
							notifyDataChanged();
							return true;
						default:
							char ch = keyev.getChar();
							if (ch >= 32 && ch < 256)
							{
								return typeChar(ch);
							}
							break;
					}
				}
			}
			else if (event instanceof GLOMouseButtonEvent)
			{
				GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
				if ( mbe.isPressed(1) )
				{
					if (ctx.getFocused() != this)
					{
						event.getContext().requestFocus(this);
						return true;
					} else {
						int charwidth = (int)setupFontServer().getTextWidth();
						int pos = (mbe.getX()-getOrigin().x+charwidth/2)/charwidth;
						setSelection(pos, pos);
						beginDrag(mbe);
						return true;
					}
				}
				else if ( mbe.isReleased(1) )
				{
					endDrag(mbe);
					return true;
				}
			}
			else if (event instanceof GLOMouseMovedEvent && isDragging())
			{
				GLOMouseMovedEvent mme = (GLOMouseMovedEvent)event;
				int pos = (int)((mme.getX()-getOrigin().x)/setupFontServer().getTextWidth());
				if (pos > selfirst)
					setSelection(selfirst, pos);
				else
					setSelection(pos, selfirst);
			}
		}

		return super.handleEvent(event);
	}

/***
	public boolean needsClipping()
	{
		return true;
	}
***/

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOLabel.class);

	static {
		prophelp.registerGetSet("text", "Text", String.class);
		prophelp.registerGetSet("text_prop", "PropertyForText", String.class);
		prophelp.registerGetSet("minchars", "MinChars", int.class);
		prophelp.registerGetSet("align", "Alignment", int.class);
		prophelp.registerGetSet("editable", "Editable", boolean.class);
	}

	public Object getProp(String key)
	{
		Object o = prophelp.getProp(this, key);
		if (o == null)
			o = super.getProp(key);
		return o;
	}

	public void setProp(String key, Object value)
	{
		try {
			prophelp.setProp(this, key, value);
		} catch (PropertyRejectedException e) {
			super.setProp(key, value);
		}
	}

}
