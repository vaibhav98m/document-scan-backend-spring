package com.document.scan.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException.ServiceUnavailable;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//import com.auth0.jwt.exceptions.IncorrectClaimException;
//import com.auth0.jwt.exceptions.JWTDecodeException;
//import com.auth0.jwt.exceptions.SignatureVerificationException;
//import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import jakarta.persistence.PersistenceException;

// @ControllerAdvice
public class CustomExcetionHandler extends ResponseEntityExceptionHandler {

	// 500 Error
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();
		Fault fault = constructFaultData(faultString, "001", "SYS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// 503 Error
	@ExceptionHandler(value = { ServiceUnavailable.class, ResourceAccessException.class })
	public ResponseEntity<Object> connectException(Exception ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();
		Fault fault = constructFaultData(faultString, "002", "SYS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	// 400 Bad Request
	@ExceptionHandler(value = { JsonParseException.class })
	public ResponseEntity<Object> handleJsonParseException(JsonParseException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();
		Fault fault = constructFaultData(faultString, "003", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// 405
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			@NonNull HttpRequestMethodNotSupportedException ex, @NonNull HttpHeaders headers,
			@NonNull HttpStatusCode status, @NonNull WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();
		Fault fault = constructFaultData(faultString, "004", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);

	}

	// 415
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(@NonNull HttpMediaTypeNotSupportedException ex,
			@NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();
		Fault fault = constructFaultData(faultString, "005", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);

	}

	// 404
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(@NonNull NoHandlerFoundException ex,
			@NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();
		Fault fault = constructFaultData(faultString, "006", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(@NonNull HttpMediaTypeNotAcceptableException ex,
			@NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();
		Fault fault = constructFaultData(faultString, "007", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
	}

	@Override
	public ResponseEntity<Object> handleMissingPathVariable(@NonNull MissingPathVariableException ex,
			@NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = "Path variable is missing: " + ex.getVariableName();
		Fault fault = constructFaultData(faultString, "012", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
			@NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		System.out.println("=============>" + ex.getLocalizedMessage());
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			Fault fault = constructFaultData(error.getDefaultMessage(), "008", "BUS", request.getHeader("uniqueId"));
			if (!errorResponse.getFaults().contains(fault)) {
				errorResponse.addNewFault(fault);
			}
		}

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
			@NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		Fault fault = null;
		String faultString = ex.getMessage();

		if (ex.getCause() instanceof UnrecognizedPropertyException) {
			UnrecognizedPropertyException unrecognizedPropertyException = (UnrecognizedPropertyException) ex.getCause();
			faultString = "Unrecognized property " + "'" + unrecognizedPropertyException.getPropertyName() + "'";
		} else if (ex.getCause() instanceof JsonParseException) {
			faultString = "Json parse error due to parsing request/response";
		} else if (ex.getCause() instanceof MismatchedInputException) {
			faultString = "Json Parse Error due to mismatched Input";
		}

		fault = constructFaultData(faultString, "009", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			@NonNull MissingServletRequestParameterException ex, @NonNull HttpHeaders headers,
			@NonNull HttpStatusCode status, @NonNull WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();
		Fault fault = constructFaultData(faultString, "010", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// 400 Bad Request
	@ExceptionHandler(value = { MissingRequestHeaderException.class })
	public ResponseEntity<Object> handleMissingRequestHeaderException(MissingRequestHeaderException ex,
			WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();
		Fault fault = constructFaultData(faultString, "011", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// Regular expression to extract ISO 8601 UTC timestamp with 'Z'
	// private static final Pattern TIMESTAMP_PATTERN =
	// Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z");

	// Method to extract the timestamp from a string
	// private String extractTimestamp(String input) {
	// Matcher matcher = TIMESTAMP_PATTERN.matcher(input);
	// if (matcher.find()) {
	// return matcher.group(); // Return the first matched timestamp
	// }
	// return null; // Return null if no match is found
	// }

	// private String formatTime(String utcTime) {
	// // Input UTC timestamp
	//
	// // Parse the UTC time
	// ZonedDateTime utcDateTime = ZonedDateTime.parse(utcTime);
	//
	// // Convert to IST
	// ZonedDateTime istDateTime =
	// utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
	//
	// // Format the IST time
	// String formattedIST =
	// istDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
	//
	// // Output
	// System.out.println("IST Time: " + formattedIST);
	//
	// return formattedIST;
	// }

	// 401 Unauthorized Exception
	// @ExceptionHandler(value = { TokenExpiredException.class })
	// public ResponseEntity<Object>
	// handleTokenExpiredException(TokenExpiredException ex, WebRequest request) {
	// ErrorResponse errorResponse = new ErrorResponse();
	//
	// String faultString = ex.getMessage();
	//
	// String time = extractTimestamp(faultString);
	//
	// if (time != null) {
	// faultString = faultString.replace(time, "");
	// time = formatTime(time);
	// faultString = faultString.substring(0, faultString.length() - 1) + time;
	//
	// }
	//
	// Fault fault = constructFaultData(faultString, "021", "BUS",
	// request.getHeader("uniqueId"));
	// errorResponse.addNewFault(fault);
	// return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	// }
	//
	// // 401 Unauthorized Exception
	// @ExceptionHandler(value = { IncorrectClaimException.class })
	// public ResponseEntity<Object>
	// handleIncorrectClaimException(IncorrectClaimException ex, WebRequest request)
	// {
	// ErrorResponse errorResponse = new ErrorResponse();
	//
	// String faultString = ex.getMessage();
	//
	// String time = extractTimestamp(faultString);
	//
	// if (time != null) {
	// faultString = faultString.replace(time, "");
	// time = formatTime(time);
	// faultString = faultString.substring(0, faultString.length() - 1) + time;
	//
	// }
	//
	// Fault fault = constructFaultData(faultString, "024", "BUS",
	// request.getHeader("uniqueId"));
	// errorResponse.addNewFault(fault);
	// return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	// }
	//
	// // 400 Bad Request
	// @ExceptionHandler(value = { JWTDecodeException.class })
	// public ResponseEntity<Object> handleJWTDecodeException(JWTDecodeException ex,
	// WebRequest request) {
	// ErrorResponse errorResponse = new ErrorResponse();
	//
	// String faultString = ex.getMessage();
	// Fault fault = constructFaultData(faultString, "022", "BUS",
	// request.getHeader("uniqueId"));
	// errorResponse.addNewFault(fault);
	// return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	// }

	// // 400 Bad Request
	// @ExceptionHandler(value = { HttpClientErrorException.class })
	// public ResponseEntity<Object>
	// handleHttpClientErrorException(HttpClientErrorException ex, WebRequest
	// request) {
	// ErrorResponse errorResponse = new ErrorResponse();
	//
	// String faultString = ex.getMessage();
	// Fault fault = constructFaultData(faultString, "025", "BUS",
	// request.getHeader("uniqueId"));
	// errorResponse.addNewFault(fault);
	// return new ResponseEntity<>(errorResponse, ex.getStatusCode());
	// }

	// 400 Bad Request
	// @ExceptionHandler(value = { SignatureVerificationException.class })
	// public ResponseEntity<Object>
	// handleSignatureVerificationExceptionException(SignatureVerificationException
	// ex,
	// WebRequest request) {
	// ErrorResponse errorResponse = new ErrorResponse();
	//
	// String faultString = ex.getMessage();
	// Fault fault = constructFaultData(faultString, "023", "BUS",
	// request.getHeader("uniqueId"));
	// errorResponse.addNewFault(fault);
	// return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	// }

	// 400 Bad Request
	@ExceptionHandler(value = { MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
			WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();
		Fault fault = constructFaultData(faultString, "011", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// 400 Bad Request
	@ExceptionHandler(value = { NumberFormatException.class })
	public ResponseEntity<Object> handleCustomNumberFormatException(NumberFormatException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = "An Invalid number entered and " + ex.getMessage();
		Fault fault = constructFaultData(faultString, "012", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// 400 Bad Request
	@ExceptionHandler(value = { PersistenceException.class })
	public ResponseEntity<Object> handleCustomPersistenceExceptionx(PersistenceException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();

		Fault fault = constructFaultData(
				faultString.equals("")
						? "A system/database constraint violation occured: "
								+ ex.getCause().fillInStackTrace().getCause().getMessage()
						: faultString,
				"013", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// 401 Access Denied
	/*
	 * @ExceptionHandler(value = { AccessDeniedException.class }) public
	 * ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex,
	 * WebRequest request) { ErrorResponse errorResponse = new ErrorResponse();
	 * String faultString = ex.getMessage(); Fault fault =
	 * constructFaultData(faultString, "014", "BUS", request.getHeader("uniqueId"));
	 * errorResponse.addNewFault(fault); return new ResponseEntity<>(errorResponse,
	 * HttpStatus.UNAUTHORIZED); }
	 */

	// 400 Bad Request
	@ExceptionHandler(value = { IllegalArgumentException.class })
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();
		Fault fault = constructFaultData(faultString, "015", "BUS", request.getHeader("uniqueId"));
		errorResponse.addNewFault(fault);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// 400 Bad Request
	@ExceptionHandler(value = { CustomExcetion.class })
	public ResponseEntity<Object> handleCustomException(Exception ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse();
		String faultString = ex.getMessage();

		Fault fault = new Fault();

		HttpStatusCode statusCode = HttpStatus.BAD_REQUEST;
		if (((CustomExcetion) ex).getStatusCode() != null) {
			statusCode = ((CustomExcetion) ex).getStatusCode();
		}

		if (ex.getCause() instanceof CustomExcetion) {
			faultString = ex.getMessage();
			fault = constructFaultData(faultString, ((CustomExcetion) ex).getErrorCode(), "BUS",
					request.getHeader("uniqueId"));

		} else {

			fault = constructFaultData(faultString, ((CustomExcetion) ex).getErrorCode(), "BUS",
					request.getHeader("uniqueId"));
		}

		errorResponse.addNewFault(fault);

		return new ResponseEntity<>(errorResponse, statusCode);
	}

	private Fault constructFaultData(String faultString, String faultCode, String faultType, String transactionCode) {
		Fault fault = new Fault();
		fault.setFaultString(faultString);
		fault.setFaultCode(faultCode);
		fault.setFaultType(faultType);
		fault.setTransactonCode(transactionCode);
		return fault;
	}
}