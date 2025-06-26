package com.document.scan.utility;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.springframework.stereotype.Component;

import com.document.scan.model.ValidationResult;

@Component
public class EmailDomainValidator {

	private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@"
			+ "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

	private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

	public ValidationResult validateEmail(String email) {
		ValidationResult result = new ValidationResult(email);

		try {
			// Step 1: Check email format
			result.setValidFormat(isValidFormat(email));
			if (!result.isValidFormat()) {
				result.setError("Invalid email format");
				return result;
			}

			// Step 2: Extract domain
			String domain = email.substring(email.indexOf('@') + 1);

			// Step 3: Check if domain resolves
			result.setDomainExists(domainExists(domain));
			if (!result.isDomainExists()) {
				result.setError("Domain does not exist");
				return result;
			}

			// Step 4: Check MX records
			List<String> mxRecords = getMxRecords(domain);
			result.setMxRecords(mxRecords);
			result.setHasMxRecord(!mxRecords.isEmpty());

			if (!result.isHasMxRecord()) {
				result.setError("No MX records found");
			}

		} catch (Exception e) {
			result.setError("Validation error: " + e.getMessage());
		}

		return result;
	}

	private boolean isValidFormat(String email) {
		return pattern.matcher(email).matches();
	}

	private boolean domainExists(String domain) {
		try {
			InetAddress.getByName(domain);
			return true;
		} catch (UnknownHostException e) {
			return false;
		}
	}

	private List<String> getMxRecords(String domain) {
		List<String> mxRecords = new ArrayList<>();

		try {
			Hashtable<String, String> env = new Hashtable<>();
			env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

			DirContext ctx = new InitialDirContext(env);
			Attributes attrs = ctx.getAttributes(domain, new String[] { "MX" });
			Attribute attr = attrs.get("MX");

			if (attr != null) {
				for (int i = 0; i < attr.size(); i++) {
					mxRecords.add(attr.get(i).toString());
				}
			}

			ctx.close();
		} catch (NamingException e) {
			System.err.println("Error getting MX records: " + e.getMessage());
		}

		return mxRecords;
	}
}