package com.herath.corexo.core.dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.util.StringUtil;
import com.herath.corexo.core.system.BaseCorexoComponent;
import com.herath.corexo.core.utils.CorexoCoreConstants;

public class DefaultMessageIdGenerator extends BaseCorexoComponent implements IMessageIdGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageIdGenerator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.herath.core.dispatch.ICorrelationIdGenerator#getCorrelationId(java.
	 * lang.String)
	 */
	public String getMessageId(String correlationIdKey) {
		IdGenerator idGenerator = null;
		if (StringUtil.isNullOrEmpty(correlationIdKey)) {
			return this.getMessageId();
		} else {
			HazelcastInstance hzInstance = this.getHazelcastInstance();
			idGenerator = hzInstance.getIdGenerator(correlationIdKey);
		}

		StringBuilder correlationId = new StringBuilder();
		correlationId
				.append(correlationIdKey)
				.append(CorexoCoreConstants.HYPHEN)
				.append(System.currentTimeMillis())
				.append(CorexoCoreConstants.HYPHEN)
				.append(idGenerator.newId());
		return correlationId.toString();
	}

	public String getMessageId() {
		HazelcastInstance hzInstance = this.getHazelcastInstance();
		IdGenerator idGenerator = hzInstance.getIdGenerator(CorexoCoreConstants.DEFAULT_PREFIX_ID_GENERATOR);

		StringBuilder correlationId = new StringBuilder();
		correlationId
				.append(CorexoCoreConstants.DEFAULT_MESSAGE_PREFIX)
				.append(CorexoCoreConstants.HYPHEN)
				.append(System.currentTimeMillis())
				.append(CorexoCoreConstants.HYPHEN)
				.append(idGenerator.newId());
		return correlationId.toString();
	}
}
