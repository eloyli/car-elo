package com.trilead.ssh2.transport;

public interface ByteCountListener {
	void updateByteCount(long in, long out);
}
