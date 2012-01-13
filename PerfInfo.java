package sample.cpu.mine;

import com.sun.btrace.annotations.*;
import com.sun.btrace.aggregation.*;
import static com.sun.btrace.BTraceUtils.*;
import java.text.DecimalFormat;
import java.util.Map;

@BTrace
public class PerfInfo {

	private static Map<String, String> full_gc_hash_map = Collections.newWeakMap();
	private static Map<String, String> minor_gc_hash_map = Collections.newWeakMap();

	@TLS
	static int loopcount = 0;
	
	@OnTimer(3000)
	public static void printAll() {

		//Start of the record
		print("{");
		
		// Current Time Stamp
		String time = Time.timestamp("yyyy-MM-dd_hh-mm-ss");
		Appendable ts = Strings.newStringBuilder();
		Strings.append(ts, "[");
		Strings.append(ts, time);
		Strings.append(ts, "];");
		println(str(ts));
	
		//VM CPU Usage
		long cpu_usage = currentProcessCpuUsage();
		Appendable cpu = Strings.newStringBuilder();
		Strings.append(cpu, "[VM CPU%:");
		Strings.append(cpu, str(cpu_usage));
		Strings.append(cpu, "%];");
		println(str(cpu));

		//Living Thread counts, without daemon Threads
		Appendable thd = Strings.newStringBuilder();
		Strings.append(thd, "[VM Threads Count:");
		Strings.append(thd, str(threadCount()- daemonThreadCount()));
		Strings.append(thd, "];");
		Strings.append(thd, "[Daemon Threads Count:");
		Strings.append(thd, str(daemonThreadCount()));
		Strings.append(thd, "];");
		println(str(thd));
		
		//Heap and Nonheap memory usage
		Appendable mem = Strings.newStringBuilder();
		Strings.append(mem, "[UsedHeap:");
		Strings.append(mem, str(used(heapUsage())));
		Strings.append(mem, "];");
		Strings.append(mem, "[Heap_Used%:");
		Strings.append(mem, str((used(heapUsage()) * 100) / max(heapUsage())));
		Strings.append(mem, "%];");
		Strings.append(mem, "[UsedNonHeap:");
		Strings.append(mem, str(used(nonHeapUsage())));
		Strings.append(mem, "];");
		Strings.append(mem, "[NonHeap_Used%:");
		Strings.append(mem, str((used(nonHeapUsage()) * 100) / max(nonHeapUsage())));
		Strings.append(mem, "%];");
		println(str(mem));
		
		//GC Activity
		Appendable gc = Strings.newStringBuilder();
		Strings.append(gc, "[Total GC time:");
		Strings.append(gc, str(getTotalGcTime()));
		Strings.append(gc, "ms];");
		Strings.append(gc, "[GC Throughput:");
		Strings.append(gc, getGCThroughput());
		Strings.append(gc, "];");
		Strings.append(gc, "[Total GC count:");
		Strings.append(gc, str(getTotalCollectionCount()));
		Strings.append(gc, "];");
		Strings.append(gc, "[Avg GC timing:");
		Strings.append(gc, str(getTotalGcTime()/getTotalCollectionCount()));
		Strings.append(gc, "ms];");
		println(str(gc));
		
		Appendable full_gc_detail = Strings.newStringBuilder();
		full_gc_hash_map = getFullGCDetailInfo();
		Strings.append(full_gc_detail, "[Full GC type:");
		Strings.append(full_gc_detail, Collections.get(full_gc_hash_map, "Full_GC_Name"));
		Strings.append(full_gc_detail, "];");
		Strings.append(full_gc_detail, "[Full GC Total Count:");
		Strings.append(full_gc_detail, Collections.get(full_gc_hash_map, "Full_GC_Total_Count"));
		Strings.append(full_gc_detail, "];");
		Strings.append(full_gc_detail, "[Full GC Total Time:");
		Strings.append(full_gc_detail, Collections.get(full_gc_hash_map, "Full_GC_Total_Time"));
		Strings.append(full_gc_detail, "ms];");
		Strings.append(full_gc_detail, "[Full GC Avg Time:");
		Strings.append(full_gc_detail, Collections.get(full_gc_hash_map, "Full_GC_Avg_Time"));
		Strings.append(full_gc_detail, "ms];");
		println(str(full_gc_detail));
		
		Appendable minor_gc_detail = Strings.newStringBuilder();
		minor_gc_hash_map = getMinorGCDetailInfo();
		Strings.append(minor_gc_detail, "[Minor GC type:");
		Strings.append(minor_gc_detail, Collections.get(minor_gc_hash_map, "Minor_GC_Name"));
		Strings.append(minor_gc_detail, "];");
		Strings.append(minor_gc_detail, "[Minor GC Total Count:");
		Strings.append(minor_gc_detail, Collections.get(minor_gc_hash_map, "Minor_GC_Total_Count"));
		Strings.append(minor_gc_detail, "];");
		Strings.append(minor_gc_detail, "[Minor GC Total Time:");
		Strings.append(minor_gc_detail, Collections.get(minor_gc_hash_map, "Minor_GC_Total_Time"));
		Strings.append(minor_gc_detail, "ms];");
		Strings.append(minor_gc_detail, "[Minor GC Avg Time:");
		Strings.append(minor_gc_detail, Collections.get(minor_gc_hash_map, "Minor_GC_Avg_Time"));
		Strings.append(minor_gc_detail, "ms];");
		println(str(minor_gc_detail));
		
		//End of the record
		println("}");
		
		// Thread Dump if CPU% > Threshold, say 50% for example
		if (cpu_usage > 50){
		// dump the Thread stack trace to local file
			String file_name = strcat("D:/threaddump",time);
			jstackAllFile(file_name);
		}
		
		// Generate Heap dump if the threshold is met
		/*loopcount = loopcount + 1;
		if(loopcount % 10 == 0 ) {
			String filename = Strings.concat(time, ".hprof");
			//only dump live obejcts to the profiled JVM dir
			dumpHeap(filename);
		}*/
		
	}

}