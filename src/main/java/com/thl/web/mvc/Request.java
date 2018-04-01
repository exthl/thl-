package com.thl.web.mvc;

public class Request {

	private String requestPath;
	
	public Request(String requestPath) {
		this.requestPath = requestPath;
	}
	

	public String getRequestPath() {
		return requestPath;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((requestPath == null) ? 0 : requestPath.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		if (requestPath == null) {
			if (other.requestPath != null)
				return false;
		} else if (!requestPath.equals(other.requestPath))
			return false;
		return true;
	}

	
}
