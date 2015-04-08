
package com.linkage.toptea.sysmgr.fm.assistant;

import java.io.DataInput; 
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public final class UID
    implements Serializable
{

    private static int getHostUniqueNum()
    {
        return (new Object()).hashCode();
    }

    public UID()
    {
        synchronized(mutex)
        {
            if(lastCount == 32767)
            {
                for(boolean done = false; !done;)
                {
                    time = System.currentTimeMillis();
                    if(time < lastTime + ONE_SECOND)
                    {
                        try
                        {
                            Thread.currentThread();
                            Thread.sleep(ONE_SECOND);
                        }
                        catch(InterruptedException e) { }
                    } else
                    {
                        lastTime = time;
                        lastCount = -32768;
                        done = true;
                    }
                }

            } else
            {
                time = lastTime;
            }
            unique = hostUnique;
            count = lastCount++;
        }
    }

    public UID(short num)
    {
        unique = 0;
        time = 0L;
        count = num;
    }

    private UID(int unique, long time, short count)
    {
        this.unique = unique;
        this.time = time;
        this.count = count;
    }

    public int hashCode()
    {
        return (int)time + count;
    }

    public boolean equals(Object obj)
    {
        if(obj != null && (obj instanceof UID))
        {
            UID uid = (UID)obj;
            return unique == uid.unique && count == uid.count && time == uid.time;
        } else
        {
            return false;
        }
    }

    public String toString()
    {
        return Integer.toString(count, 36) + Long.toString(time, 36) + Integer.toString(unique, 36);
    }

    public void write(DataOutput out)
        throws IOException
    {
        out.writeInt(unique);
        out.writeLong(time);
        out.writeShort(count);
    }

    public static UID read(DataInput in)
        throws IOException
    {
        int unique = in.readInt();
        long time = in.readLong();
        short count = in.readShort();
        return new UID(unique, time, count);
    }

    private int unique;
    private long time;
    private short count;
    private static final long serialVersionUID = 0xf12700dbf364f12L;
    private static int hostUnique = getHostUniqueNum();
    private static long lastTime = System.currentTimeMillis();
    private static short lastCount = -32768;
    private static Object mutex = new Object();
    private static long ONE_SECOND = 1000L;

}
