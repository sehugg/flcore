   /**
* This file is part of NanoXML.
 *
 * $Revision: 1.2 $
 * $Date: 2008-03-15 15:54:02 $
 * $Name:  $
 *
 * Copyright (C) 2000 Marc De Scheemaecker, All Rights Reserved.
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software in
 *     a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 *
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *
 *  3. This notice may not be removed or altered from any source distribution.
 */
package com.fasterlight.util;

import java.io.PrintWriter;

public class XMLUtil
{

   public static void writeXML(PrintWriter out,
                               String      str)
   {
      for (int i = 0; i < str.length(); i++)
         {
            char ch = str.charAt(i);

            switch (ch)
               {
                  case '<':
                     out.write("&lt;");
                     break;

                  case '>':
                     out.write("&gt;");
                     break;

                  case '&':
                     out.write("&amp;");
                     break;

                  case '"':
                     out.write("&quot;");
                     break;

                  case '\'':
                     out.write("&apos;");
                     break;

                  case '\r':
                  case '\n':
                     out.write(ch);
                     break;

                  default:
                     if ((ch < 32) || (ch > 126))
                        {
                           out.write("&#x");
                           out.write(Integer.toString(ch, 16));
                           out.write(';');
                        }
                     else
                        {
                           out.write(ch);
                        }
                     break;
               }
         }
   }

}
