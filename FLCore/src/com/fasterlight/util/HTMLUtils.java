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
package com.fasterlight.util;

import java.net.*;

public class HTMLUtils
{
   public static final String parseCDATA(String s)
   {
      int l = s.length();
      if (l >= 2)
      {
         char c1 = s.charAt(0);
         char c2 = s.charAt(l-1);
         if (c1 == c2 && (c1 == '\'' || c1 == '"'))
         {
            return s.substring(1, l-1);
         }
      }
      return s;
   }

   public static final URL getCanonical(URL url)
   throws MalformedURLException
   {
      String file = url.getFile();
      if (file == null || file.length() == 0)
         file = "/";
      return new URL(
         url.getProtocol().toLowerCase(),
         url.getHost().toLowerCase(),
         url.getPort(),
         file);
   }
}

