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

import java.awt.HeadlessException;
import java.awt.event.*;
import java.util.Iterator;

import javax.swing.*;

/**
 * Used to use native menus with a GLOMenu.
 */
public class MenuedFrame extends JFrame
{
	private GLOCommandManager cmdmgr;
	private GLOMenu menu;
	private boolean useNativeMenu;

	public MenuedFrame(String title) throws HeadlessException
	{
		super(title);
	}

	public void refreshMenu()
	{
		if (!useNativeMenu)
			return;
		JMenuBar awtMenuBar = new JMenuBar();
		awtMenuBar.setName(this.getTitle());
		if (menu != null)
		{
			Iterator it = menu.getItems().iterator();
			while (it.hasNext())
			{
				GLOMenuItem item = (GLOMenuItem) it.next();
				JMenu awtMenu = new JMenu(item.getText());
				fillMenuItems(awtMenu, item.getSubMenu());
				awtMenuBar.add(awtMenu);
			}
		}
		this.setJMenuBar(awtMenuBar);
		// TODO: this kills Linux
		this.validate();
	}

	private void fillMenuItems(JMenu awtMenu, GLOMenu gloMenu)
	{
		Iterator it = gloMenu.getItems().iterator();
		while (it.hasNext())
		{
			GLOMenuItem gloItem = (GLOMenuItem) it.next();
			JMenuItem awtItem;
			// regular item, submenu, or separator?
			if ("---".equals(gloItem.getText()))
			{
				awtMenu.addSeparator();
				continue;
			} else if (gloItem.getSubMenu() != null)
			{
				JMenu subMenu = new JMenu(gloItem.getText());
				fillMenuItems(subMenu, gloItem.getSubMenu());
				awtItem = subMenu;
			} else
			{
				awtItem = new JMenuItem(gloItem.getText());
			}
			// add the action listener
			linkActionToMenuItem(gloItem, awtItem);
			awtMenu.add(awtItem);
		}
	}

	private void linkActionToMenuItem(GLOMenuItem gloItem, JMenuItem awtItem)
	{
		final Object action = gloItem.getAction();
		if (action != null)
		{
			awtItem.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					if (cmdmgr != null)
						cmdmgr.execute(action.toString());
				}

			});
			// find shortcut, if exists
			if (cmdmgr != null)
			{
				int keycode = cmdmgr.getKeyCodeForCommand(action.toString());
				int menumask = GLOCommandManager.getPlatformKeyMask();
				if (keycode > 0 && (keycode & menumask) != 0)
				{
					int awtkeycode = keycode & ~GLOCommandManager.MODS_MASK;
					int awtmods = (keycode & GLOCommandManager.MODS_MASK) >> 16;
					awtItem.setAccelerator(
						KeyStroke.getKeyStroke(awtkeycode, awtmods));
				}
			}
		}
	}

	public static void main(String[] args) throws Exception
	{
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		GLOMenu menu = new GLOMenu();
		menu.load("panels/main.mnu");
		GLOCommandManager cmdmgr = new GLOCommandManager(null);
		cmdmgr.loadCommands("commands/commands.txt");
		cmdmgr.loadControlBindings("commands/keys.txt");

		MenuedFrame mf = new MenuedFrame("GLO Menu Test");
		mf.setCmdmgr(cmdmgr);
		mf.setGLOMenu(menu);
		mf.show();
		mf.refreshMenu();
	}

	public void setGLOMenu(GLOMenu menu)
	{
		System.out.println("Setting menu: " + menu);
		this.menu = menu;
		this.useNativeMenu = true;
		refreshMenu();
	}

	public GLOMenu getMenu()
	{
		return menu;
	}

	public void setCmdmgr(GLOCommandManager cmdmgr)
	{
		System.out.println("Setting cmdmgr: " + cmdmgr);
		this.cmdmgr = cmdmgr;
		refreshMenu();
	}

	public GLOCommandManager getCmdmgr()
	{
		return cmdmgr;
	}
}
