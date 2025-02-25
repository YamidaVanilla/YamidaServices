package xyz.yamida.services.profile.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(1)
class IpFilter : Filter {

    private val allowedIps = setOf(
        "87.120.186.145",
        "85.254.73.33",
        "213.226.141.213",
        "127.0.0.1"
    )

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val clientIp = httpRequest.getHeader("X-Forwarded-For") ?: httpRequest.remoteAddr

        if (allowedIps.contains(clientIp)) {
            chain.doFilter(request, response)
        } else {
            httpResponse.status = HttpServletResponse.SC_FORBIDDEN
            httpResponse.writer.write("Access Denied for IP: $clientIp")
        }
    }
}
