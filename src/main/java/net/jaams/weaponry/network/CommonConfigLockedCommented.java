package net.jaams.weaponry.network;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;

/**
 * Read-only view over a synced (server-provided) commented config.
 * Every mutating operation is rejected so client-side runtime editors
 * (e.g. Configured) cannot alter values that belong to the server.
 */
public class CommonConfigLockedCommented extends UnmodifiableConfigWrapper<UnmodifiableCommentedConfig> implements CommentedConfig {
	public CommonConfigLockedCommented(UnmodifiableCommentedConfig backing) {
		super(backing);
	}

	@Override
	public <T> T set(List<String> path, Object value) {
		throw serverControlled();
	}

	@Override
	public boolean add(List<String> path, Object value) {
		throw serverControlled();
	}

	@Override
	public <T> T remove(List<String> path) {
		throw serverControlled();
	}

	@Override
	public void clear() {
		throw serverControlled();
	}

	@Override
	public CommentedConfig createSubConfig() {
		throw serverControlled();
	}

	@Override
	public String getComment(List<String> path) {
		return config.getComment(path);
	}

	@Override
	public boolean containsComment(List<String> path) {
		return config.containsComment(path);
	}

	@Override
	public Map<String, String> commentMap() {
		return Collections.unmodifiableMap(config.commentMap());
	}

	@Override
	public Map<String, UnmodifiableCommentedConfig.CommentNode> getComments() {
		return config.getComments();
	}

	@Override
	public String setComment(List<String> path, String comment) {
		throw serverControlled();
	}

	@Override
	public String removeComment(List<String> path) {
		throw serverControlled();
	}

	@Override
	public void clearComments() {
		throw serverControlled();
	}

	@Override
	public void putAllComments(Map<String, UnmodifiableCommentedConfig.CommentNode> comments) {
		throw serverControlled();
	}

	@Override
	public void putAllComments(UnmodifiableCommentedConfig commentedConfig) {
		throw serverControlled();
	}

	@Override
	public UnmodifiableCommentedConfig unmodifiable() {
		return config;
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Set<? extends CommentedConfig.Entry> entrySet() {
		return (Set) config.entrySet();
	}

	@Override
	public Map<String, Object> valueMap() {
		return Collections.unmodifiableMap(config.valueMap());
	}

	private static UnsupportedOperationException serverControlled() {
		return new UnsupportedOperationException("This config is controlled by the server and cannot be changed client-side");
	}
}
