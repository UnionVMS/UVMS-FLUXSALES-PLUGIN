# FLUX-Sales plugin

####Module description/purpose

This is a plugin for the Exchange module.

The purpose is to make it possible to exchange sales messages over FLUX.

#### Supported messages:
* Receive
  * FLUXSalesReportMessage
  * FLUXSalesQueryMessage
  * FLUXSalesResponseMessage
* Send
  * FLUXSalesReportMessage
  * FLUXSalesResponseMessage
  
#### How to use it
1. Configure your FLUX TL to redirect messages with DF *urn:un:unece:uncefact:fisheries:FLUX:SALES:EU:2* to the SOAP endpoint *[server-path]/flux-sales-service-1.0.0/SalesService/FluxMessageReceiverBean*
1. Deploy this plugin, the Exchange, Rules and Sales module (and their dependencies).