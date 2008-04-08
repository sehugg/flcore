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

import java.io.*;
import java.util.*;

import com.fasterlight.spif.*;

public class INIFile
{
   private String filename;
   private File file;
   private byte[] bytes;
   private boolean doEscapes=true;

   public INIFile(String filename)
   {
      this.filename = filename;
   }

   public INIFile(File file)
   {
      this.file = file;
   }

   public void setDoEscapes(boolean b)
   {
   	this.doEscapes=b;
   }

   public boolean getDoEscapes(boolean b)
   {
   	return doEscapes;
   }

   protected String escape(String s)
   {
   	return doEscapes ? StringUtil.escape(s) : s;
   }

   protected String unescape(String s)
   {
   	return doEscapes ? StringUtil.unescape(s) : s;
   }

	/**
	 * Copies an InputStream to a temporary file and uses that
	 */
   public INIFile(InputStream instream)
   throws IOException
   {
   	InputStream in = instream;
   	if (!(in instanceof BufferedInputStream))
   		in = new BufferedInputStream(in);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int c;
      while ((c=in.read()) != -1)
      {
      	out.write(c);
      }
      this.bytes = out.toByteArray();
      out.close();
      instream.close();
   }

   protected BufferedReader getReader()
   throws IOException
   {
   	if (bytes != null)
   		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
      if (file == null)
         file = new File(filename);
      if (!file.exists())
         return new BufferedReader(new StringReader(""));
      else
         return new BufferedReader(new FileReader(file));
   }

   protected void writeFile(Object o, boolean append)
   throws IOException
   {
      FileWriter bw;
      if (filename == null)
      {
      	if (file != null)
	         bw = new FileWriter(file.getCanonicalPath(), append);
	      else
	      	throw new RuntimeException("There is no filename to write!");
      } else
         bw = new FileWriter(filename, append);
      bw.write(o.toString());
      bw.close();
   }

   private static String CRLF = System.getProperty("line.separator");

   public String setString(String section, String key, String newvalue)
   throws IOException
   {
      BufferedReader br = getReader();
      try {
         StringBuffer sb = new StringBuffer();
         String line;
         String cursection = "";
         String oldvalue = null;
         boolean writeline;
         boolean foundsection = false;
         boolean foundkey = false;
         boolean changed = false;
         boolean insection = false;
         while ((line = br.readLine()) != null)
         {
            writeline = true;
            if (line.length() >= 1)
            {
               switch (line.charAt(0))
               {
                  case ';' :
                     break; // comment
                  case '[' :
                     cursection = line.substring(1, line.indexOf(']', 1));
                     insection = cursection.equalsIgnoreCase(section);
                     if (insection)
                        foundsection = true;
                     break;
                  default :
                     if (insection)
                     {
                        int pos = line.indexOf('=');
                        if (pos > 0)
                        {
                           String curkey = line.substring(0, pos);
                           if (curkey.equalsIgnoreCase(key))
                           {
                              // if we already found it, and changed it...
                              // comment it out
                              if (foundkey && changed)
                              {
                                 sb.append(';');
                              } else {
                                 foundkey = true;
                                 oldvalue = "";
                                 if (line.length() > pos)
                                    oldvalue = line.substring(pos + 1);
                                 if (!oldvalue.equals(newvalue))
                                 {
                                    // if newvalue == null, delete line
                                    if (newvalue != null)
                                    {
                                       sb.append(curkey);
                                       sb.append('=');
                                       sb.append(escape(newvalue));
                                       sb.append(CRLF);
                                    }
                                    writeline = false;
                                    changed = true;
                                 }
                              }
                           }
                        }
                        break;
                     }
               }
            } else {
               // if we got a blank line, and
               // we were in the desired section but didn't find anything,
               // add a key=value line
               if (insection && !foundkey)
               {
                  sb.append(key);
                  sb.append('=');
                  sb.append(escape(newvalue));
                  sb.append(CRLF);
                  changed = true;
                  foundkey = true; // well, we sorta did...
               }
            }
            if (writeline)
            {
               sb.append(line);
               sb.append(CRLF);
            }
         }
         // if changed, write it out
         if (changed)
         {
            writeFile(sb, false);
         }
         if (!foundkey)
         {
            sb = new StringBuffer();
            // if we didn't find the section, make a new one
            // and append it
            if (!foundsection)
            {
               sb.append(CRLF + '[' + section + ']' + CRLF);
               insection = true;
            }
            // if we are currently in the section, and no key is found
            // just append the key
            if (insection)
            {
               sb.append(key);
               sb.append('=');
               sb.append(escape(newvalue));
               sb.append(CRLF);
            }
            if (sb.length() > 0)
            {
               writeFile(sb, true);
            }
         }
         return oldvalue;
      } finally {
         br.close();
      }
   }

   public String getString(String section, String key, String defvalue)
   throws IOException
   {
      BufferedReader br = getReader();
      try {
         String line;
         String cursection;
         boolean insection = false;
         while ((line = br.readLine()) != null)
         {
            if (line.length() >= 1)
            {
               switch (line.charAt(0))
               {
                  case ';' :
                     break; // comment
                  case '[' :
                     cursection = line.substring(1, line.indexOf(']', 1));
                     insection = cursection.equalsIgnoreCase(section);
                     break;
                  default :
                     if (insection)
                     {
                        int pos = line.indexOf('=');
                        if (pos > 0)
                        {
                           String curkey = line.substring(0, pos);
                           if (curkey.equalsIgnoreCase(key))
                           {
                              String oldvalue = "";
                              if (line.length() > pos)
                                 oldvalue = unescape(line.substring(pos + 1));
                              return oldvalue;
                           }
                        }
                     }
                     break;
               }
            }
         }
         return defvalue;
      } finally {
         br.close();
      }
   }

   public Properties getSection(String section)
   throws IOException
   {
      BufferedReader br = getReader();
      try {
         Properties hash = new Properties();
         String line;
         String cursection;
         boolean insection = false;
         while ((line = br.readLine()) != null)
         {
            if (line.length() >= 1)
            {
               switch (line.charAt(0))
               {
                  case ';' :
                     break; // comment
                  case '[' :
                     if (insection)
                        return hash;
                     cursection = line.substring(1, line.indexOf(']', 1));
                     insection = cursection.equalsIgnoreCase(section);
                     break;
                  default :
                     if (insection)
                     {
                        int pos = line.indexOf('=');
                        if (pos > 0)
                        {
                           String curkey = line.substring(0, pos);
                           String oldvalue = "";
                           if (line.length() > pos)
                              oldvalue = unescape(line.substring(pos + 1));
                           hash.put(curkey, oldvalue);
                        }
                     }
                     break;
               }
            }
         }
         return hash;
      } finally {
         br.close();
      }
   }

   public List getSectionNames()
   throws IOException
   {
      BufferedReader br = getReader();
      try {
      	List v = new ArrayList();
         String line;
         String cursection;
         while ((line = br.readLine()) != null)
         {
            if (line.length() >= 1)
            {
               switch (line.charAt(0))
               {
                  case ';' :
                     break; // comment
                  case '[' :
                     cursection = line.substring(1, line.indexOf(']', 1));
                     v.add(cursection);
                     break;
                  default :
                  	break;
               }
            }
         }
         return v;
      } finally {
         br.close();
      }
   }

   public int getInt(String section, String name, int defvalue)
   throws IOException
   {
   	try {
   		return PropertyUtil.toInt(getString(section, name, Integer.toString(defvalue)));
   	} catch (PropertyRejectedException nfe) {
   		return defvalue;
   	}
   }

   public long getLong(String section, String name, long defvalue)
   throws IOException
   {
   	try {
   		return PropertyUtil.toLong(getString(section, name, Long.toString(defvalue)));
   	} catch (PropertyRejectedException nfe) {
   		return defvalue;
   	}
   }

   public float getFloat(String section, String name, float defvalue)
   throws IOException
   {
   	try {
   		return PropertyUtil.toFloat(getString(section, name, Float.toString(defvalue)));
   	} catch (PropertyRejectedException nfe) {
   		return defvalue;
   	}
   }

   public double getDouble(String section, String name, double defvalue)
   throws IOException
   {
   	try {
   		return PropertyUtil.toDouble(getString(section, name, Double.toString(defvalue)));
   	} catch (PropertyRejectedException nfe) {
   		return defvalue;
   	}
   }

   public boolean getBoolean(String section, String name, boolean defvalue)
   throws IOException
   {
   	try {
   		return PropertyUtil.toBoolean(getString(section, name, defvalue?"true":"false"));
   	} catch (PropertyRejectedException nfe) {
   		return defvalue;
   	}
   }

   public void flush()
   {
   }

   public static void main(String[] args)
   throws Exception
   {
      INIFile ini = new INIFile(args[0]);
      System.out.println("Setting [" + args[1] + "]" + args[2] + "=" + args[3]);
      System.out.println("Old value=" + ini.setString(args[1], args[2], args[3]));
      System.out.println("New value=" + ini.getString(args[1], args[2], null));
   }
}

