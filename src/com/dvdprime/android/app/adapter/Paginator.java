package com.dvdprime.android.app.adapter;

public interface Paginator {

	/**
	 * Method for request next page of products if exists.
	 * 
	 * @return the more
	 */
	boolean getMore();

	/**
	 * Setter for base model adapter.
	 * 
	 * @param adapter
	 *            the new adapter
	 */
	void setAdapter(BaseModelAdapter adapter);

	boolean canLoadMore();
	
	boolean isLoading();
}
