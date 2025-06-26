package com.document.scan.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Fault {
	private String faultString;
	private String faultCode;
	private String transactonCode;
	private String faultType;
}
