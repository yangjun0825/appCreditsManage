package com.app.inter;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface appRequestHandlerService {
	
	public String requestHandlerEnternce(@WebParam(name = "name")String xmlStr);
	
}
